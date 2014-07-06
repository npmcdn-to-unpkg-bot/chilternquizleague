mainApp.controller('LeagueTableController', [ '$scope', '$interval',
		'viewService', function($scope, $interval, viewService) {

			$scope.$watch("global", function(globalData) {
				if (globalData) {
					function loadTable() {
						viewService.view("leaguetable", {
							id : globalData.currentSeasonId
						}, function(ret) {
							$scope.season = ret;
						});
					}
					loadTable();
					$interval(loadTable, 30000, 60)["catch"](function() {
						$interval.cancel(this);
					});
				}
			});

		} ]);
