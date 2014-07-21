mainApp.controller('LeagueTableController', [ '$scope', '$interval',
		'viewService', function($scope, $interval, viewService) {

			$scope.$watch("global.currentSeasonId", function(currentSeasonId) {
				if (currentSeasonId) {
					function loadTable() {
						$scope.season = viewService.view("leaguetable", {
							id : currentSeasonId
						});
					}
					loadTable();
					$interval(loadTable, 30000, 60)["catch"](function() {
						$interval.cancel(this);
					});
				}
			});

		} ]);
