package org.chilternquizleague.json;

import org.chilternquizleague.domain.TeamJava;
import org.junit.Before;
import org.junit.Test;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;

public class JsonTests {

	Gson gson;
	
	@Before
	public void setUp() throws Exception {
		
		gson = new Gson();
	}

	@Test
	public void test() throws Exception{
		TeamJava team = new TeamJava();
		team.setName("The Squirrel");
		team.setShortName("Squirrel");
		ObjectMapper objectMapper = new ObjectMapper();
		
		
		System.out.println(objectMapper.writeValueAsString(team));
	}


}
