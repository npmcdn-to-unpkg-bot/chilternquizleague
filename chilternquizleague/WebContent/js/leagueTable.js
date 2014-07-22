mainApp.controller('LeagueTableController', [ '$scope', '$interval',
		'viewService', function($scope, $interval, viewService) {

			$scope.$watch("global.currentSeasonId", function(currentSeasonId) {
				if (currentSeasonId) {
					function loadTable() {
						viewService.view("leaguetable", {
							id : currentSeasonId
						},function(season){$scope.season=season;});
					}
					loadTable();
					$interval(loadTable, 3000, 60)["catch"](function() {
						$interval.cancel(this);
					});
				}
			});

		} ]);
