(function() {
	
	mainApp.run(function ($rootScope) {
    $rootScope.$on('$locationChangeSuccess', function () {
        $rootScope.$broadcast("pathChanged");
    });
});
	
	
	var templateMap = {
		detail : "team-details.html",
		results : "team-results.html",
		fixtures : "team-fixtures.html",
		reports: "/results/reports.html"
			
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

		
		
		function getTemplateName(){
			
			var parts = $location.path().split("/");
			
			return parts[2] ? parts[2] : "detail";
		}
		

			$scope.setTemplate = function(templateName) {
				var template = templateMap[templateName];
				if (template != $scope.template) {
	
					$scope.template = template;
					var parts = $location.path().split("/");
	
					$location.path(parts[1] + "/" + templateName);
				}
			};
		

		
		$scope.setTemplate(getTemplateName());
		
		$scope.$on("pathChanged", function(){
			$scope.setTemplate(getTemplateName());	
		});
		
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
				
				$scope.headerText = viewService.text("teams-header", $scope.global);
				
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
	
	mainApp.controller("TeamExtrasController", [ '$scope','$interval' ,'viewService',
		'$location',  function($scope, $interval,viewService, $location) {
			
			$scope.setSeason = function(season){$scope.season = season;};

			var loadSeasons = listAndSelection("season", $scope, viewService,{remoteListName:"season-views"});
			
			function teamExtras(){
				
				if($scope.team && $scope.season){
					viewService.view("team-extras", {
						seasonId : $scope.season.id,
						teamId : $scope.team.id
					}, function(extras){
						if($scope.team.id != extras.id){
						$scope.team.extras = extras;}});

				}
			}
			
			loadSeasons($scope.global.currentSeasonId);
			
			$scope.$watch("global.currentSeasonId", loadSeasons);
			$scope.$watch("season", teamExtras);
			$scope.$watch("team.id", teamExtras);
			
			$scope.showReports = function(result){
				
				//$scope.reports = viewService.view("result-reports", )
				
			};
			
		} ]);



})();
