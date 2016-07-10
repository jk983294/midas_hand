package com.victor.midas.services;

import javax.ws.rs.*;
import javax.ws.rs.core.*;

import com.victor.midas.model.common.CmdType;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;

@Path("admin")
public class AdminService {
	
	
	@Autowired
	private TaskMgr taskMgr;
	
	private static final Logger logger = Logger.getLogger(AdminService.class);
	
	@PUT
	@Path("/stocks")
	public Response updateStocks() {		
		taskMgr.cmd(CmdType.load, new ArrayList<String>());
		return Response.ok().build();
	}
	
	/**
	 * deliver task to delete all stocks in MongoDB
	 * @return
	 */
	@DELETE
	@Path("/stocks")
	public Response deleteStocks() {	
		taskMgr.cmd(CmdType.delete, new ArrayList<String>());
		return Response.ok().build();
	}
	
	
	
}
