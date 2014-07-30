package org.chilternquizleague.views;

import java.util.ArrayList;
import java.util.List;

import org.chilternquizleague.domain.Report;
import org.chilternquizleague.domain.Result;
import org.chilternquizleague.domain.Team;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility;

@JsonAutoDetect(fieldVisibility = Visibility.ANY)
public class ResultsReportsView {
	
	
	
	private List<ReportView> reports = new ArrayList<>();
	
	public ResultsReportsView(Result result){

		for(Report report : result.getReports()){
			
			reports.add(new ReportView(report.getTeam(), report.getText().getText()));
		}
	}

				
				

	@JsonAutoDetect(fieldVisibility = Visibility.ANY)
	public static class ReportView{
		
		public ReportView(Team team, String report) {
			super();
			this.team = team;
			this.text = report;
		}
		private Team team;
		private String text;
	}
}
