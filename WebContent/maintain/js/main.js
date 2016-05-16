
var maintainApp = angular.module('maintainApp', ['ngMaterial','ui.router', "ngAnimate","ui.tinymce", "ngComponentRouter"])
		.factory('entityService', ENTITY_SERVICE_DEFN);


maintainApp.config(['$locationProvider', function($locationProvider) {

/*
	$stateProvider.state("home", {
	    url: "/maintain",
	    templateUrl: '/maintain/dummy.html'
	})
	.state("venues", {
		url : "/maintain/venues",
		templateUrl : "/maintain/venue/venue-list.html"
	})
	.state("venue", {
		url : "/maintain/venues/:venueId",
		templateUrl : '/maintain/venue/venue-detail.html'
	})
	.state("teams", {
		url : "/maintain/teams",
		templateUrl : "/maintain/team/team-list.html"
	})
	.state("team", {
		url : "/maintain/teams/:teamId",
		templateUrl : '/maintain/team/team-detail.html'
	})

	.state('users', {
		templateUrl : '/maintain/user/user-list.html',
		url : '/maintain/users'})
	.state('user', {
		templateUrl : '/maintain/user/user-detail.html',
		url : '/maintain/users/:userId'})
	.state('seasons', {
		templateUrl : '/maintain/season/season-list.html',
		url : '/maintain/seasons'})
	.state("season", {
		templateUrl : '/maintain/season/season-detail.html'})
	.state("season.detail", {
		templateUrl:"/maintain/season/season-detail-contents.html",
		url : '/maintain/seasons/:seasonId'}
	)
	.state('season.calendar', {
		templateUrl : '/maintain/season/calendar.html',
		url: "/maintain/seasons/:seasonId/calendar"}
	)
	.state('season.competition', {
		templateUrl : '/maintain/competition/competition-container.html',
		url: "/maintain/seasons/:seasonId/competition"}
	)
	.state("season.competition.detail", {
		templateUrl : function(params){return "/maintain/competition/" + params.compType.toLowerCase() + "-detail.html"},
		url : "/:compType"}
	)

	.state("season.competition.fixtures", {
		templateUrl : '/maintain/competition/fixtures.html',
		url : '/:compType/fixtures'})
	.state("season.competition.results", {
		templateUrl : '/maintain/competition/results.html',
		url : '/:compType/results'})
	.state("season.competition.tables", {
		templateUrl : '/maintain/competition/tables.html',
		url : '/:compType/tables'})
	.state("global", {
		templateUrl : '/maintain/global/global-detail.html',
		url : '/maintain/global/current'})
	.state('texts', {
		templateUrl : '/maintain/text/text-list.html',
		url : '/maintain/texts'})
	.state('text', {
		templateUrl : '/maintain/text/text-detail.html',
		url : '/maintain/texts/:textId'})
	.state('stats', {
		templateUrl : '/maintain/stats/season-list.html',
		url : '/maintain/stats'})
	.state('stats-detail', {
		templateUrl : '/maintain/stats/stats-detail.html',
		url: "/maintain/stats/:seasonId"})

	.state("database", {
		templateUrl : '/maintain/database/database.html',
		url:'/maintain/database'})
	.state('mail', {
		templateUrl : '/maintain/mail/mail-options.html',
		url : '/maintain/mail'})
	.state('mass-mail', {
		templateUrl : '/maintain/mail/mass-mail.html',
		url : '/maintain/mail/mass-mail'})

	$locationProvider.html5Mode(true);
	
*/
	
	
} ]);

maintainApp.controller("MainCtrl",[ '$mdSidenav',"$scope", function($mdSidenav,$scope) {

	  $scope.toggleLeft = function() {
		    $mdSidenav('left').toggle();
		  };
		  
		  $scope.$on("progress", function(ev,value){$scope.progress = value;});
}]);

maintainApp.directive("cqlAddButton",function(){
	
	return {
		restrict:'E',
		replace:true,
		template : "<span><md-button class='md-raised' ng-click='addScreen()'>Add New</md-button></span>"};
});

maintainApp.value('$routerRootComponent', 'app')

function makeUpdateFn(typeName, noRedirect) {
	return makeUpdateFnWithCallback(typeName, (noRedirect ? null : function(
			ret, $location) {
		$location.url("/maintain/" + typeName + "s");
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
		config = config ? config : {};
		
		var collectionName = (config.collName ? config.collName
				: typeName + "s")

		entityService.loadList(typeName, function(ret) {
			$scope[collectionName] = config.sort ? ret.sort(config.sort) : ret;
		});

		$scope.addScreen = function() {
			$location.path("/maintain/" + collectionName + "/new");
		};

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

function filter(list, fn){
	
	var ret = []
	
	for(idx in list){
		if(fn(list[idx])){
			ret.push(list[idx])
		}
	}
	
	return ret;
}

function getCommonParams(constructorFn) {

	return [ '$scope', 'entityService', '$rootScope',
			'$location', constructorFn ];
}

var tinymceOptions={ 
		plugins: "link, image, autolink, table, code, charmap, searchreplace, contextmenu",
		menubar:true,
	    toolbar: "undo redo | styleselect | bold italic | alignleft aligncenter alignright alignjustify | bullist numlist | link image"
	
};
