
var maintainApp = angular.module('maintainApp', [ "ngRoute", "ngAnimate" ])
		.factory('entityService', ENTITY_SERVICE_DEFN);
;

maintainApp.config([ '$routeProvider', function($routeProvider) {
	$routeProvider.when('/venues', {
		templateUrl : 'maintain/venue/venue-list.html',
		controller : 'VenueListCtrl'
	}).when('/venues/:venueId', {
		templateUrl : 'maintain/venue/venue-detail.html',
		controller : 'VenueDetailCtrl'
	}).when('/teams', {
		templateUrl : 'maintain/team/team-list.html',
		controller : 'TeamListCtrl'
	}).when('/teams/:teamId', {
		templateUrl : 'maintain/team/team-detail.html',
		controller : 'TeamDetailCtrl'
	}).when('/users', {
		templateUrl : 'maintain/user/user-list.html',
		controller : 'UserListCtrl'
	}).when('/users/:userId', {
		templateUrl : 'maintain/user/user-detail.html',
		controller : 'UserDetailCtrl'
	}).when('/seasons', {
		templateUrl : 'maintain/season/season-list.html',
		controller : 'SeasonListCtrl'
	}).when('/seasons/:seasonId', {
		templateUrl : 'maintain/season/season-detail.html',
		controller : 'SeasonDetailCtrl'
	}).when('/seasons/:seasonId/LEAGUE', {
		templateUrl : 'maintain/competition/league-detail.html',
		controller : 'LeagueCompCtrl'
	}).when('/seasons/:seasonId/:compType/fixtures', {
		templateUrl : 'maintain/competition/fixtures.html',
		controller : 'FixturesCtrl'
	}).when('/seasons/:seasonId/LEAGUE/results', {
		templateUrl : 'maintain/competition/results.html',
		controller : 'ResultsCtrl'
	}).when('/seasons/:seasonId/:compType/tables', {
		templateUrl : 'maintain/competition/tables.html',
		controller : 'LeagueTablesCtrl'
	}).when('/global/current', {
		templateUrl : 'maintain/global/global-detail.html',
		controller : 'GlobalDetailCtrl'
	}).when('/texts', {
		templateUrl : 'maintain/text/text-list.html',
		controller : 'TextListCtrl'
	}).when('/texts/:textId', {
		templateUrl : 'maintain/text/text-detail.html',
		controller : 'TextDetailCtrl'
	}).otherwise({
		redirectTo : ''
	});
} ]);

function makeUpdateFn(typeName, noRedirect) {
	return makeUpdateFnWithCallback(typeName, (noRedirect ? null : function(
			ret, $location) {
		$location.url("/" + typeName + "s");
	}));
}

/**
 * 
 */
function makeUpdateFnWithCallback(typeName, saveCallback, loadCallback) {

	var camelName = typeName.charAt(0).toUpperCase() + typeName.substr(1);

	var masterName = "master" + camelName;
	var resetName = "reset" + camelName;

	return function($scope, entityService, $routeParams, $rootScope, $location) {

		var id = $routeParams[typeName + "Id"];

		$scope[resetName] = function() {
			$scope[typeName] = angular.copy($scope[masterName]);
		};

		entityService.load(typeName, id, function(ret) {
			$scope[masterName] = ret;
			$scope[resetName]();
			loadCallback ? loadCallback(ret) : null;
		});

		$scope["update" + camelName] = function(entity) {

			$scope[masterName] = angular.copy(entity);
			entityService.remove(typeName, id);
			entityService.save(typeName, entity, function(ret) {
				saveCallback ? saveCallback(ret, $location) : null;
			});

		};

	};

}

function makeListFn(typeName, config) {

	return function($scope, entityService) {
		var collectionName = typeName + "s";
		config = config ? config : {};

		if (config.entityName && config.bindName) {
			collectionName = (config.collName ? config.collName
					: collectionName);
			$scope.$watch(config.entityName, function(entity) {
				syncToListItem($scope, entity, $scope[collectionName],
						config.bindName);
			});
			$scope.$watch(collectionName, function(collection) {
				syncToListItem($scope, $scope[config.entityName], collection,
						config.bindName);
			});

		}
		entityService.loadList(typeName, function(ret) {
			$scope[collectionName] = config.sort ? ret.sort(config.sort) : ret;
		});
	};
}

function syncToListItem($scope, entity, collection, propName) {

	if (entity && entity[propName] && collection) {

		for (index in collection) {

			if (entity[propName].id == collection[index].id) {

				entity[propName] = collection[index];
				break;
			}
		}
	}

}

function removeFromListById(collection, entity) {

	for (index in collection) {
		if (collection[index].id == entity.id) {
			collection.splice(index, 1);
			break;
		}
	}
}

function getCommonParams(constructorFn) {

	return [ '$scope', 'entityService', '$routeParams', '$rootScope',
			'$location', constructorFn ];
}