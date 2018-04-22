package com.victor.visualization.endpoint;

import com.victor.visualization.model.TypeAheadResponse;
import com.victor.visualization.services.FileService;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.ws.rs.GET;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/visual")
public class FileEndpoint {

    private final Logger logger = Logger.getLogger(FileEndpoint.class);

    @Autowired
    private FileService fileService;

    @GET
    @RequestMapping("/table/{path}")
    @Produces(MediaType.APPLICATION_JSON)
    public Map<String, Object> getTable(@PathVariable("path") String path) throws IOException {
        logger.info(path + " getTable ");
        return fileService.getCsvContent(path);
    }

    @GET
    @RequestMapping("/typeahead/{query}")
    @Produces(MediaType.APPLICATION_JSON)
    public TypeAheadResponse getTips(@PathVariable("query") String query) {
        List<String> tips = fileService.getPossibleFiles(query);
        return new TypeAheadResponse(tips, "SUCCESS");
    }
}
