(function(){
	
	function mapToProperty(arr,propName){
		return arr.map(function(i){return i==null ? null : i[propName]});
	}
	
	function nullIgnorables(i){return i.ignorable ? null : i}
	

mainApp.controller("TeamCharts",["$scope", 'viewService',"$filter", function($scope,viewService, $filter){
		$scope.setCurrentItem();
		function dateToLabel(dates){
			return dates.map(function(d){return $filter("date")(new Date(d),"dd MMM")});
		}

		
		$scope.positionOptions={
				scaleStartValue:10,
				scaleStepWidth:-1,
				scaleOverride:true,
				scaleSteps:9,
				datasetFill : false,
				bezierCurve : true
				};
		
		$scope.options={
				datasetFill : true,
				bezierCurve : true,
				scaleBeginAtZero : false,
				legendTemplate : '<ul class="chart-legend"><% for (var i=0; i<datasets.length; i++){%><li><span style="background-color:<%=datasets[i].strokeColor%>"></span><%if(datasets[i].label){%><%=datasets[i].label%><%}%></li><%}%></ul>',
			    datasetStroke : true,	
			};
		
		$scope.differenceOptions={
				datasetFill : false,
				bezierCurve : true,
				scaleBeginAtZero : false,
			    datasetStroke : true,	
			};
		
		function loadStats(newValues){
			
			if(newValues && newValues.length > 1 && newValues[0] && newValues[1]){
				
				doLoadStats(newValues[0], newValues[1])
			}
			
		}
		
		function doLoadStats(team, season){
			if (season.id ){
			viewService.view("team-statistics",{
								seasonId : season.id,
								teamId : team.id
							}, function(stats){
								
								var weekStats = stats.weekStats
								weekStats.sort(function(s1,s2){return s1.date - s2.date;});
								var dateLabels = dateToLabel(mapToProperty(weekStats, "date").sort());
								
								$scope.positionData = {
										labels: dateLabels,
										responsive:true,
										datasets: [{
											label:team.shortName,
											strokeColor: "rgba(220,220,220,1)",
											pointColor : "rgba(220,220,220,1)",
											fillColor: "rgba(220,220,220,0.2)",
								            pointStrokeColor: "#fff",
								            pointHighlightFill: "#fff",
								            pointHighlightStroke: "rgba(220,220,220,1)",
											data: mapToProperty(weekStats, "leaguePosition") 
										}]
								};
								

								
								$scope.pointsData = {
										labels: dateLabels,
										responsive:true,
										datasets: [{
											label:"For",
											strokeColor: "rgba(220,220,220,1)",
											fillColor: "rgba(220,220,220,0.2)",
											pointColor : "rgba(220,220,220,1)",
								            pointStrokeColor: "#fff",
								            pointHighlightFill: "#fff",
								            pointHighlightStroke: "rgba(220,220,220,1)",
											data: mapToProperty(weekStats.map(nullIgnorables), "pointsFor")
										},{
											strokeColor: "rgba(205,50,50,1)",
											fillColor: "rgba(151,187,205,0.2)",
											pointColor : "rgba(205,50,50,1)",
											label:"Against",
								            pointStrokeColor: "#fff",
								            pointHighlightFill: "#fff",
								            pointHighlightStroke: "rgba(205,50,50,1)",
											data: mapToProperty(weekStats.map(nullIgnorables), "pointsAgainst") 
										}]	
								};
								
								$scope.cumPointsData = {
										labels: dateLabels,
										responsive:true,
										datasets: [{
											label:"For",
											strokeColor: "rgba(220,220,220,1)",
											pointColor : "rgba(220,220,220,1)",
											fillColor: "rgba(220,220,220,0.2)",
								            pointStrokeColor: "#fff",
								            pointHighlightFill: "#fff",
								            pointHighlightStroke: "rgba(220,220,220,1)",
											data: mapToProperty(weekStats.map(nullIgnorables), "cumuPointsFor") 
										},{
											label:"Against",
											strokeColor: "rgba(205,50,50,1)",
											fillColor: "rgba(151,187,205,0.2)",
											pointColor : "rgba(205,50,50,1)",
								            pointStrokeColor: "#fff",
								            pointHighlightFill: "#fff",
								            pointHighlightStroke: "rgba(205,50,50,1)",
											data: mapToProperty(weekStats.map(nullIgnorables), "cumuPointsAgainst") 
										}]	
										
								};
								
								$scope.cumPointsDifferenceData = {
										labels: dateLabels,
										responsive:true,
										datasets: [{
											label:"Difference",
											strokeColor: "rgba(220,220,220,1)",
											pointColor : "rgba(220,220,220,1)",
											fillColor: "rgba(220,220,220,0.2)",
								            pointStrokeColor: "#fff",
								            pointHighlightFill: "#fff",
								            pointHighlightStroke: "rgba(220,220,220,1)",

											data: mapToProperty(weekStats.map(nullIgnorables), "cumuPointsDifference") 
										}]	
										
								};
								

							});
				}
			}
		
		$scope.$watchGroup(["team","season"], loadStats);
	}]);


mainApp.controller("AllSeasonCharts",["$scope", 'viewService',"$filter", "seasonService", "$q",function($scope,viewService, $filter, seasonService, $q){
	$scope.setCurrentItem();
	function dateToLabel(dates){
		return dates.map(function(d){return $filter("date")(new Date(d),"dd MMM")});
	}
	
	$scope.positionOptions={
			scaleStartValue:10,
			scaleStepWidth:-1,
			scaleOverride:true,
			scaleSteps:9,
			datasetFill : false,
			bezierCurve : true
			};
	
	$scope.options={
			datasetFill : true,
			bezierCurve : true,
			scaleBeginAtZero : false,
			legendTemplate : '<ul class="chart-legend"><% for (var i=0; i<datasets.length; i++){%><li><span style="background-color:<%=datasets[i].strokeColor%>"></span><%if(datasets[i].label){%><%=datasets[i].label%><%}%></li><%}%></ul>',
		    datasetStroke : true,	
		};
	
	$scope.differenceOptions={
			datasetFill : false,
			bezierCurve : true,
			scaleBeginAtZero : false,
		    datasetStroke : true,	
		};
	
	function loadStats(team){
		
		if(team){
			
			doLoadStats(team)
		}
		
	}
	
	function doLoadStats(team){
		seasonService.getSeasons().then(
				function(seasons){
					return $q.all(seasons.map(function(season){
						return viewService.viewP("team-statistics",{
							seasonId : season.id,
							teamId : team.id})
					}))
				}
		).then(
				function(statsSet){
					statsSet = statsSet.filter(function(stats){return stats != null}).sort(function(stats1, stats2){return stats1.season.description.localeCompare(stats2.season.description) })
					var seasonStats = statsSet.map(function(stats){
						stats.seasonStats.count = stats.weekStats.filter(function(week){return !week.ignorable}).length; 
						return stats.seasonStats})
						
					var years = statsSet.map(function(stats){return stats.season.description})
					
					$scope.positionData = {
						labels: years,
						responsive:true,
						datasets: [{
							label:team.shortName,
							strokeColor: "rgba(220,220,220,1)",
							pointColor : "rgba(220,220,220,1)",
							fillColor: "rgba(220,220,220,0.2)",
				            pointStrokeColor: "#fff",
				            pointHighlightFill: "#fff",
				            pointHighlightStroke: "rgba(220,220,220,1)",
							data: mapToProperty(seasonStats, "currentLeaguePosition") 
						}]
				};
				
					var averageScores = seasonStats.map(function(stats){return {"for":stats.runningPointsFor / stats.count, against:stats.runningPointsAgainst / stats.count}})
					$scope.pointsData = {
							labels: years,
							responsive:true,
							datasets: [{
								label:"For",
								strokeColor: "rgba(220,220,220,1)",
								fillColor: "rgba(220,220,220,0.2)",
								pointColor : "rgba(220,220,220,1)",
					            pointStrokeColor: "#fff",
					            pointHighlightFill: "#fff",
					            pointHighlightStroke: "rgba(220,220,220,1)",
								data: mapToProperty(averageScores, "for")
							},{
								strokeColor: "rgba(205,50,50,1)",
								fillColor: "rgba(151,187,205,0.2)",
								pointColor : "rgba(205,50,50,1)",
								label:"Against",
					            pointStrokeColor: "#fff",
					            pointHighlightFill: "#fff",
					            pointHighlightStroke: "rgba(205,50,50,1)",
								data: mapToProperty(averageScores, "against") 
							}]	
					};

				
				
				}
		
		)
	}
		
		$scope.$watch("team", loadStats);
}]);

mainApp.controller("AllTeamsCharts",["$scope", 'viewService',"$filter", "seasonService", "$q",function($scope,viewService, $filter, seasonService, $q){
	$scope.setCurrentItem();
	function dateToLabel(dates){
		return dates.map(function(d){return $filter("date")(new Date(d),"dd MMM")});
	}
	
	$scope.positionOptions={
			scaleStartValue:10,
			scaleStepWidth:-1,
			scaleOverride:true,
			scaleSteps:9,
			datasetFill : false,
			bezierCurve : true,
			legendTemplate : '<ul class="chart-legend"><% for (var i=0; i<datasets.length; i++){%><li><span style="background-color:<%=datasets[i].strokeColor%>"></span><%if(datasets[i].label){%><%=datasets[i].label%><%}%></li><%}%></ul>',
	    datasetStroke : true,	
			};
	
	$scope.options={
			datasetFill : true,
			bezierCurve : true,
			scaleBeginAtZero : false,
			legendTemplate : '<ul class="chart-legend"><% for (var i=0; i<datasets.length; i++){%><li><span style="background-color:<%=datasets[i].strokeColor%>"></span><%if(datasets[i].label){%><%=datasets[i].label%><%}%></li><%}%></ul>',
		    datasetStroke : true,	
		};
	
	$scope.differenceOptions={
			datasetFill : false,
			bezierCurve : true,
			scaleBeginAtZero : false,
		    datasetStroke : true,	
		};
	
	function loadStats(values){
		
		if(values[0] && values[1]){
			
			doLoadStats(values[0], values[1])
		}
		
	}
	
	function doLoadStats(teams, season){
		
		function getRandomColor() {
	    var letters = '0123456789ABCDEF'.split('');
	    var color = '#';
	    for (var i = 0; i < 6; i++ ) {
	        color += letters[Math.floor(Math.random() * 16)];
	    }
	    return color;
	}
		
		$q.all(teams.map(function(team){return viewService.viewP("team-statistics",{
							seasonId : season.id,
							teamId : team.id})}))
							.then(
									function(statsSet){
										statsSet = statsSet.filter(function(stats){return stats != null}).sort(function(stats1, stats2){return stats1.team.name.localeCompare(stats2.team.name) })
										var dateLabels = dateToLabel(mapToProperty(statsSet[0].weekStats, "date").sort());

										var datasets = statsSet.map(function(stats){
											
											var col = getRandomColor()
											 return {
												label:stats.team.shortName,
												strokeColor: col,
												pointColor : col,
												      pointStrokeColor: "#fff",
									            pointHighlightFill: "#fff",
									            pointHighlightStroke: col,
												data: mapToProperty(stats.weekStats.sort(function(s1,s2){return s1.date - s2.date;}), "leaguePosition") 
											}
										});
										
										$scope.positionData = {
												labels: dateLabels,
												responsive:true,
												datasets: datasets
									}
									}
							)
		
	
				}
		

		
	$scope.$watchGroup(["teams","season"], loadStats);
}]);
})()