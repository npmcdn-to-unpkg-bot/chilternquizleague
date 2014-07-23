mainApp.controller('CompetitionsController', [ '$scope', '$location',
		'viewService', function($scope, $location, viewService) {

			$scope.$watch("global.currentSeasonId", function(currentSeasonId) {
				if (currentSeasonId) {
					$scope.competitions = viewService.view("competitions-view",{id:currentSeasonId,isArray:true});
				}
			});

		} ]);