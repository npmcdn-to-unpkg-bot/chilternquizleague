
var maintainApp = angular.module('maintainApp', ['ngMaterial',"ngAnimate","ui.tinymce", "ngComponentRouter"])
		.factory('entityService', ENTITY_SERVICE_DEFN)
		.factory("ctrlUtil", ["$location", "$rootRouter", "$rootScope", "entityService", 
				function($location, $rootRouter, $rootScope, entityService){
			
			
			
			var service = {
					
					camelCase : function (typeName){
						return typeName.charAt(0).toUpperCase() + typeName.substr(1);
					},
					
					bindToParent : bindToParent,
					addWatchFn : addWatchFn,
					
					newEntity : function(typeName){
						return entityService.load(typeName,"new")
					}
	,				
					makeUpdateFn : function(typeName, $scope, ctrlfn, saveCallback,loadCallback){
							service.makeLoadFn(typeName, $scope,ctrlfn, loadCallback)
							service.makeFormFns(typeName, $scope, ctrlfn, saveCallback)
					},
					
					makeLoadFn : 		function(typeName, $scope, ctrlfn, loadCallback){
						var camelName = service.camelCase(typeName);

						var masterName = "master" + camelName;
						var resetName = "reset" + camelName;

						var ctrl = ctrlfn ? ctrlfn : this
							
							var roaFn = ctrl.$routerOnActivate
							
							roaFn = roaFn ? roaFn : function(){};
									
							ctrl.$routerOnActivate = function(next){
								var id = next.params[typeName + "Id"];
								
								$scope[typeName + "Id"] = id

								entityService.load(typeName, id, function(ret) {
									$scope[masterName] = ret;
									$scope[resetName]();
									loadCallback ? loadCallback(ret) : null;
								});


								roaFn(next)
							}},
					
					makeFormFns : function(typeName, $scope, ctrlfn, saveCallback) {
						var camelName = service.camelCase(typeName)

						var masterName = "master" + camelName;
						var resetName = "reset" + camelName;
						
						
						var ctrl = ctrlfn ? ctrlfn : this
						
						var roaFn = ctrl.$routerOnActivate
						
						roaFn = roaFn ? roaFn : function(){};
								
						ctrl.$routerOnActivate = function(next){
							var id = next.params[typeName + "Id"];
							
							$scope[typeName + "Id"] = id
							
							$scope[resetName] = function() {
								$scope[typeName] = angular.copy($scope[masterName]);
							};

							$scope["update" + camelName] = function(entity) {

								$scope[masterName] = angular.copy(entity);
								entityService.remove(typeName, id);
								entityService.save(typeName, entity, function(ret) {
									saveCallback ? saveCallback(ret, $rootRouter) : null;
								}).then(function(entity){$scope[masterName] = entity;
													$rootRouter.navigate(["Root",camelName + "s"])
								});

							};
							roaFn(next)}
						},
						makeListFn : function makeListFn(typeName,$scope, config) {

							config = config ? config : {};
							
							var collectionName = (config.collName ? config.collName
									: typeName + "s")

							entityService.loadList(typeName, function(ret) {
								$scope[collectionName] = config.sort ? ret.sort(config.sort) : ret;
							});

							$scope.addScreen = function() {
								$location.path("" + collectionName + "/new");
							};

				
					}
			}
			return service
		}]);


maintainApp.config(['$locationProvider', function($locationProvider) {


	$locationProvider.html5Mode(true);

	
	
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
		$location.url(typeName + "s");
	}));
}

/**
 * 
 */
function makeUpdateFnWithCallback(typeName, saveCallback, loadCallback) {

	var camelName = typeName.charAt(0).toUpperCase() + typeName.substr(1);

	var masterName = "master" + camelName;
	var resetName = "reset" + camelName;

	return function($scope, entityService, $rootScope, $location, ctrlfn) {

		var ctrl = ctrlfn ? ctrlfn : this
		
		var roaFn = ctrl.$routerOnActivate
		
		roaFn = roaFn ? roaFn : function(){};
				
		ctrl.$routerOnActivate = function(next){
			var id = next.params[typeName + "Id"];
			
			$scope[typeName + "Id"] = id
			
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
			roaFn(next)
		}
		


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
			$location.path("" + collectionName + "/new");
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

	return [ '$scope', 'ctrlUtil', '$rootRouter', constructorFn ];
}

var tinymceOptions={ 
		plugins: "link, image, autolink, table, code, charmap, searchreplace, contextmenu",
		menubar:true,
	    toolbar: "undo redo | styleselect | bold italic | alignleft aligncenter alignright alignjustify | bullist numlist | link image"
	
};

function bindToParent(name,scope,ctrl, parentName){
	

	var oninit = ctrl.$onInit ? ctrl.$onInit : function(){}
	
	var ondelete = ctrl.$onDelete ? ctrl.$onDelete : function(){}
	
	var deregs = []
	
	ctrl.$onInit = function(){
		var pName = parentName ? parentName : "parent"
		deregs.push(ctrl[pName].watch(name, function(value){
			scope[name] = value
			}));
		oninit();
	}
	
	ctrl.$onDelete = function(){
		deregs.forEach(function(i){i()})
		ondelete()
	}
}

function addWatchFn(scope,ctrl){
	ctrl.watch = function(name, ln){return scope.$watch(name,ln)}
}
