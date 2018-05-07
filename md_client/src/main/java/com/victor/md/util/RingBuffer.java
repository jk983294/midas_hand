package com.victor.md.util;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.channels.FileChannel;
import java.nio.channels.FileChannel.MapMode;
import java.nio.file.StandardOpenOption;

public class RingBuffer {
    private String fileName;
    private int size;
    private int n;
    private boolean owner;

    private ByteBuffer buf;
    private long address;

    private static final int PAGE_SIZE = 4096;
    private static final int META_SIZE_SHM = PAGE_SIZE;
    private static final String MAGIC_SHM = "MIDASshm";

    private static int offset = 192;
    private static final int MAGIC_CB = 0x00108023;
    private static final int META_SIZE_CB = 192;
    private static final int MAGIC_POSITION_CB = META_SIZE_SHM;
    private static final int META_SIZE_POSITION_CB = META_SIZE_SHM + 4;
    private static final int CAPACITY_POSITION = META_SIZE_SHM + 8;
    private static final int ELEMENT_SIZE_POSITION = META_SIZE_SHM + 12;
    private static final int OFFSET_POSITION = META_SIZE_SHM + 16;
    private static final int PER_RECORD_SIZE_POSITION = META_SIZE_SHM + 20;
    private static final int INITED_POSITION = META_SIZE_SHM + 24;
    private static final int READER_POSITION = META_SIZE_SHM + 64;
    private static final int WRITER_POSITION = META_SIZE_SHM + 128;
    private static final int WRAP_POSITION = META_SIZE_SHM + 132;

    private volatile int readPos;
    private volatile int writePos;

    private static final String PATH = "/dev/shm/";

    /**
     * @param size: size of one block
     * @param n:    number of block
     *              The real buffer size will be size * n + header, the header should be same with the C++ circular buffer
     */
    private RingBuffer(String fileName, int size, int n) {
        this.fileName = fileName;
        this.size = size;
        this.n = n;
    }

    private RingBuffer(String fileName) {
        this.fileName = fileName;
    }

    public boolean write(byte[] target) {
        int readPos = getReadPosition();
        int writePos = getWritePosition();

        int writeSize = target.length;

        if (writePos < readPos) {
            if (writePos + writeSize < readPos) { // |XXXW     RXXXX|
                setBufPosition(writePos);
                buf.put(target);
                setWritePosition(writePos + writeSize);
                return true;
            } else
                return false; // no space
        } else {  // |   RXXXXXW    |
            if ((writeSize < getCapacity() - writePos) || ((writeSize == getCapacity() - writePos) && (readPos != 0))) {
                setBufPosition(writePos);
                buf.put(target);
                setWritePosition(writePos + writeSize);
                return true;
            } else if (writeSize < readPos) {
                setBufPosition(0);
                buf.put(target);
                setWrap(writePos);
                setWritePosition(writeSize);
                return true;
            } else
                return false; // no space
        }
    }

    /**
     * return the readable length of next readable piece in buffer, and set buffer's position as the read position.
     * e.g. |XXXXW     RXXXX| the whole readable pieces are the "X" marked position
     * this function will return the length from the R (read position) to the tail of this buffer
     */
    public int getNextReadableSet() {
        readPos = getReadPosition();
        writePos = getWritePosition();

        if (writePos >= readPos) {
            setBufPosition(readPos);
            return writePos - readPos;
        } else {
            int wrap = getWrap();
            if (readPos == wrap) {
                setReadPosition(0); // back to zero
                setBufPosition(0);
                return writePos;
            }
            setBufPosition(readPos);
            return wrap - readPos;
        }
    }

    /**
     * return the readable length of next readable piece in buffer.
     * e.g. |XXXXW     RXXXX| the whole readable pieces are the "X" marked position
     * this function will return the length from the R (read position) to the tail of this buffer
     */
    public int getNextReadableLength() {
        readPos = getReadPosition();
        writePos = getWritePosition();

        if (writePos >= readPos) {
            return writePos - readPos;
        } else {
            int wrap = getWrap();
            if (readPos == wrap) {
                setReadPosition(0); // back to zero
                return writePos;
            }
            return wrap - readPos;
        }
    }

    /**
     * return the whole length of the readable piece in buffer
     * e.g. |XXXXW     RXXXX| will return the R to the tail plus the header to W (write position)
     */
    public int getWholeReadableLength() {
        readPos = getReadPosition();
        writePos = getWritePosition();

        if (writePos >= readPos) {
            return writePos - readPos;
        } else {
            int wrap = getWrap();
            if (readPos == wrap) {
                return writePos;
            }
            return wrap - readPos + writePos;
        }
    }

    /**
     * read numbers of bytes from the circular buffer
     *
     * @param length : The length user wants to read
     * @return -1 if cannot read from the circular buffer or a positive number stands for the offset that can read from
     */
    public int read(int length) {
        assert (length > 0);
        readPos = getReadPosition();
        writePos = getWritePosition();
        if (writePos >= readPos) {
            if (length <= (writePos - readPos))
                return META_SIZE_SHM + META_SIZE_CB + readPos;
        } else {
            int wrap = getWrap();
            if (readPos == wrap) {
                setReadPosition(0); // back to zero
                if (length <= writePos)
                    return META_SIZE_SHM + META_SIZE_CB;
            } else {
                if (length <= wrap - readPos)
                    return META_SIZE_SHM + META_SIZE_CB + readPos;
            }
        }
        return -1;
    }

    /**
     * whether still have readable pieces
     */
    public boolean isEmpty() {
        readPos = getReadPosition();
        writePos = getWritePosition();
        return readPos == writePos;
    }

    /**
     * Consume the data has been read. Update the read position as the original value plus the length. (Same with C++ side Circular Buffer)
     *
     * @param length: the length have been consumed by reader
     */
    public void consume(int length) {
        setReadPosition(getReadPosition() + length);
    }

    /**
     * return the size of per RecordBlock (set as generic type)
     */
    public int getPerRecordSize() {
        return size;
    }

    public static RingBuffer initialiseSharedMemory(String fileName, int size, int n) throws IOException {
        RingBuffer cb = new RingBuffer(fileName, size, n);
        cb.initialize();
        return cb;
    }

    private void initialize() throws IOException {
        int mapSize = size * n;
        if (mapSize > 0) {
            File shmFile = new File(PATH, fileName);
            shmFile.delete();
            shmFile.createNewFile();
            shmFile.deleteOnExit();

            FileChannel fileChannel = new RandomAccessFile(shmFile, "rw").getChannel();
            buf = fileChannel.map(MapMode.READ_WRITE, 0, META_SIZE_SHM + META_SIZE_CB + mapSize);
            buf.order(ByteOrder.LITTLE_ENDIAN);

            setInited(0);

            //write meta into shared memory, same with the C++ shared memory
            buf.put(MAGIC_SHM.getBytes()); // MAGIC number
            buf.putInt(mapSize); // m_size
            buf.putLong(0); // m_version
            buf.putLong(0); // pid for Java, no public API for this yet until Java 9

            buildCircularBufferMeta(); // 192 bytes

            try {
                address = makeAddress();
            } catch (NoSuchMethodException | SecurityException | IllegalArgumentException | InvocationTargetException | IllegalAccessException e) {
                throw new IOException(e.getMessage());
            }

            owner = true;
            setInited(1);
        } else {
            throw new IllegalArgumentException("size or number cannot be empty");
        }
    }

    public static RingBuffer attachSharedMemory(String fileName) throws IOException, IllegalAccessException {
        RingBuffer cb = new RingBuffer(fileName);
        cb.attach();
        return cb;
    }

    private void attach() throws IOException, IllegalAccessException {
        File attachFile = new File(PATH + fileName);
        FileChannel channel = FileChannel.open(attachFile.toPath(), StandardOpenOption.WRITE, StandardOpenOption.READ);

        buf = channel.map(MapMode.READ_WRITE, 0, attachFile.length());
        buf.order(ByteOrder.LITTLE_ENDIAN);

        int retry = 0;
        while (getInited() != 1) {
            if (retry == 5) {
                throw new IllegalAccessException("Cannot attach to the memory because the file is not inited");
            }

            try {
                Thread.sleep(10);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            retry++;
        }

        this.size = getPerRecordSizeB();
        if (size != 0)
            this.n = getCapacity() / size;

        //check magic number
        buf.position(0);
        byte[] magic_num = new byte[8];
        buf.get(magic_num);
        if (MAGIC_SHM.compareTo(new String(magic_num)) != 0) {
            throw new IllegalAccessException("Cannot attach to the memory because the MAGIC num is wrong");
        }

        try {
            address = makeAddress();
        } catch (NoSuchMethodException | SecurityException | IllegalArgumentException | InvocationTargetException | IllegalAccessException e) {
            throw new IOException(e.getMessage());
        }

        owner = false;
        offset = getOffset();
    }

    public void reclaimSharedMemory() throws IOException {
        File attachFile = new File(PATH + fileName);
        attachFile.deleteOnExit();
        FileChannel channel = FileChannel.open(attachFile.toPath(), StandardOpenOption.WRITE, StandardOpenOption.READ);
        buf = channel.map(MapMode.READ_WRITE, 0, attachFile.length());
        buf.order(ByteOrder.LITTLE_ENDIAN);

        try {
            address = makeAddress();
        } catch (NoSuchMethodException | SecurityException | IllegalArgumentException | InvocationTargetException | IllegalAccessException e) {
            throw new IOException(e.getMessage());
        }

        owner = true;
        offset = getOffset();
    }

    public boolean isOwner() {
        return owner;
    }

    public boolean isValid() {
        return buf.capacity() != 0;
    }

    /**
     * For unsafe byte buffer use
     *
     * @return memory address of the direct byte buffer
     */
    public long getAddress() {
        return address;
    }

    /**
     * For nio byte buffer
     *
     * @return nio byte buffer
     */
    public ByteBuffer getBuffer() {
        return buf;
    }

    /**
     * Get the file name
     *
     * @return the file name that circular buffer created or attached
     */
    public String getFileName() {
        return fileName;
    }

    public void setSize(int size) {
        this.size = size;
        this.n = getCapacity() / size;
    }

    /**
     * return the write position of the circular buffer
     */
    private int getWritePosition() {
        return buf.getInt(WRITER_POSITION);
    }

    /**
     * return the read position of the circular buffer
     */
    private int getReadPosition() {
        return buf.getInt(READER_POSITION);
    }

    private void setBufPosition(int value) {
        buf.position(META_SIZE_SHM + META_SIZE_CB + value);
    }

    private void setMagic() {
        buf.putInt(MAGIC_POSITION_CB, MAGIC_CB);
    }

    private void setMetaSize() {
        buf.putInt(META_SIZE_POSITION_CB, META_SIZE_CB);
    }

    private void setCapacity(int value) {
        buf.putInt(CAPACITY_POSITION, value);
    }

    private int getCapacity() {
        return buf.getInt(CAPACITY_POSITION);
    }

    private void setWrap(int value) {
        buf.putInt(WRAP_POSITION, value);
    }

    private int getWrap() {
        return buf.getInt(WRAP_POSITION);
    }

    private void setElementSize(int value) {
        buf.putInt(ELEMENT_SIZE_POSITION, value);
    }

    private int getElementSize() {
        return buf.getInt(ELEMENT_SIZE_POSITION);
    }

    private void setOffset(int value) {
        buf.putInt(OFFSET_POSITION, value);
    }

    private int getOffset() {
        return buf.getInt(OFFSET_POSITION);
    }

    private void setPerRecordSizeB(int value) {
        buf.putInt(PER_RECORD_SIZE_POSITION, value);
    }

    private int getPerRecordSizeB() {
        return buf.getInt(PER_RECORD_SIZE_POSITION);
    }

    private void setReadPosition(int value) {
        buf.putInt(READER_POSITION, value);
    }

    private void setWritePosition(int value) {
        buf.putInt(WRITER_POSITION, value);
    }

    private void setInited(int value) {
        buf.putInt(INITED_POSITION, value);
    }

    private int getInited() {
        return buf.getInt(INITED_POSITION);
    }

    private void buildCircularBufferMeta() {
        setMagic();
        setMetaSize();
        setCapacity(buf.capacity() - META_SIZE_SHM - META_SIZE_CB);
        setElementSize(getPerRecordSize());
        setOffset(offset);
        setPerRecordSizeB(getPerRecordSize());
        setReadPosition(0);
        setWritePosition(0);
        setWrap(0);
    }

    /**
     * Get the memory address of the direct byte buffer (off heap)
     */
    private long makeAddress() throws NoSuchMethodException, SecurityException, IllegalAccessException,
            IllegalArgumentException, InvocationTargetException {
        Method addM = buf.getClass().getMethod("address");
        addM.setAccessible(true);
        return (long) addM.invoke(buf);
    }

}
