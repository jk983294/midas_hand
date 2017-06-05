package com.victor.utilities.report.excel.generator.common;

import org.apache.log4j.Logger;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import javax.ws.rs.core.Response;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

/**
 * base class for spreadsheet generation template, stream mode
 */
public abstract class ReportXssfBase {

    private static Logger logger = Logger.getLogger(ReportXssfBase.class);

    protected XSSFWorkbook wb;

    protected String filename;                          // the filename to export to

    public ReportXssfBase(String filename) {
        this.filename = filename;
        wb = new XSSFWorkbook();
    }

    /**
     * Generates excel export
     * @return Response - byte data of generated file
     * @throws com.victor.utilities.report.excel.generator.common.ReportException
     */
    public Response generateWorkbook() throws ReportException {
        try {

            writeTabs();

            byte[] data = write(wb);
            return createResponse(data, filename, "application/excel");
        }catch (Exception ie) {
            logger.error("generate workbook failed.", ie);
            throw new ReportException(ie);
        }
    }

    /**
     * sub class need to implement how to write each tab
     */
    public abstract void writeTabs() throws ReportException;


    /**
     * convert Workbook to byte array
     * @param workbook
     * @return
     * @throws ReportException
     */
    private byte[] write(Workbook workbook) throws ReportException {
        try {
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            workbook.write(stream);
            return stream.toByteArray();
        } catch (IOException e) {
            logger.error("Error during xls creation", e);
            throw new ReportException("Error during xls creation", e);
        }
    }

    private Response createResponse(byte[] rawBytes, String filename, String contentType) {
        Response.ResponseBuilder responseBuilder = Response.ok();
        responseBuilder.header("Content-Type", contentType);
        responseBuilder.header("Content-Disposition", String.format("attachment; filename=\"%s\"", filename));
        responseBuilder.header("Access-Control-Expose-Headers", "Content-Disposition");
        responseBuilder.entity(rawBytes);
        return responseBuilder.build();
    }

}