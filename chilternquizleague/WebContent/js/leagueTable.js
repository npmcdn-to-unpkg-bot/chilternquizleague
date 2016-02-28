mainApp.controller('LeagueTableController', [ '$scope', '$interval',
		'viewService','seasonService', function($scope, $interval, viewService,seasonService) {

			seasonService.getGlobal().then(function(global) {
				
					function loadTable() {
						viewService.view("leaguetable", {
							id : global.currentSeasonId
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

				
			})
						

		} ]);
