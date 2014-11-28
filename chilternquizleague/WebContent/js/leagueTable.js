mainApp.controller('LeagueTableController', [ '$scope', '$interval',
		'viewService', function($scope, $interval, viewService) {

			$scope.$watch("global.currentSeasonId", function(currentSeasonId) {
				if (currentSeasonId) {
					function loadTable() {
						viewService.view("leaguetable", {
							id : currentSeasonId
						},function(leagueTable){
							$scope.leagueTable=leagueTable;});
					}
					loadTable();
					var p = $interval(loadTable, 30000, 60);
					p["catch"](function() {
						$interval.cancel(p);
					});
					
					$scope.$on("$destroy", function () {
						$interval.cancel(p);
					    });

				}
			});
			

		} ]);
