(function() {
	
	var templateMap = {
		detail : "team-details.html",
		results : "team-results.html",
		fixtures : "team-fixtures.html"
			
	};
	
	
	
	mainApp.filter('afterNow', function() {
		return function(input) {
			var now = new Date().getTime();
			var ret = [];
			for (idx in input) {
				if (input[idx].start >= now) {
					ret.push(input[idx]);
				}
			}

			return ret;
		};
	});

	function extraStuff($scope, $interval, viewService, $location) {

		
		$scope.setTemplate = function(templateName){
			$scope.template = templateMap[templateName];
			var parts = $location.path().split("/");
			
			$location.path(parts[1]+ "/" + templateName);
			};
		
		var parts = $location.path().split("/");
		
		var templateName = parts[2] ? parts[2] : "detail";
		
		$scope.setTemplate(templateName);
		
		$scope.makeICal = function(team) {

			var contents = generateICalContent(team.extras.fixtures);

			var filename = team.shortName.replace(/\s/g, "_") + "_fixtures"
					+ ".ics";

			var blob = new Blob([ contents ], {
				type : "text/calendar;charset=utf-8"
			});

			saveAs(blob, filename);

		};
	
		$scope.$watch("global.currentSeasonId", function(currentSeasonId) {

			if (currentSeasonId) {
				$scope.$watch("team", function(team) {

					if (team) {
						if (!team.extras) {
							team.extras = viewService.view("team-extras", {
								seasonId : currentSeasonId,
								teamId : team.id
							});
						}
					}
				});

			}
		});
	}

	mainApp.controller('TeamsController', [ '$scope', '$interval', 'viewService',
			'$location', cyclingListControllerFactory("team", function(team1, team2) {
				return team1.shortName.localeCompare(team2.shortName);
			}, extraStuff) ]);
	
	mainApp.controller("TeamResultsController", [ '$scope','$interval' ,'viewService',
		'$location',  function($scope, $interval,viewService, $location) {
			
			$scope.setSeason = function(season){$scope.season = season;};

			var loadSeasons = listAndSelection("season", $scope, viewService,{remoteListName:"season-views"});
			
			function teamExtras(){
				
				if($scope.team && $scope.season){
					$scope.team.extras = viewService.view("team-extras", {
						seasonId : $scope.season.id,
						teamId : $scope.team.id
					});
					
				}
			}
			
			loadSeasons($scope.global.currentSeasonId);
			
			$scope.$watch("global.currentSeasonId", loadSeasons);
			$scope.$watch("season", teamExtras);
			$scope.$watch("team.id", teamExtras);
			
			
		} ]);

})();
