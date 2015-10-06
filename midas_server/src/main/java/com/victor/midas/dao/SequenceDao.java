package com.victor.midas.dao;

import com.victor.midas.model.db.misc.SequenceId;
import com.victor.midas.util.MidasException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.FindAndModifyOptions;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Component;

/**
 * get auto increment id
 */
@Component
public class SequenceDao {

    @Autowired
    private MongoOperations mongoOperation;

    public long getNextSequenceId(String key) throws MidasException {
        //get sequence id
        Query query = new Query(Criteria.where("_id").is(key));

        //increase sequence id by 1
        Update update = new Update();
        update.inc("seq", 1);

        //return new increased id
        FindAndModifyOptions options = new FindAndModifyOptions();
        options.returnNew(true);

        //this is the magic happened.
        SequenceId seqId = mongoOperation.findAndModify(query, update, options, SequenceId.class);

        //if no id, throws Exception
        //optional, just a way to tell user when the sequence id is failed to generate.
        if (seqId == null) {
            throw new MidasException("Unable to get sequence id for key : " + key);
        }

        return seqId.getSeq();
    }

    public long getLatestSequenceId(String key) throws MidasException {
        //get sequence id
        Query query = new Query(Criteria.where("_id").is(key));

        //this is the magic happened.
        SequenceId seqId = mongoOperation.findOne(query, SequenceId.class);

        //if no id, throws Exception
        //optional, just a way to tell user when the sequence id is failed to generate.
        if (seqId == null) {
            throw new MidasException("Unable to get sequence id for key : " + key);
        }

        return seqId.getSeq();
    }

}
