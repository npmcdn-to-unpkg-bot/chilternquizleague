var mainApp = angular.module('mainApp', ["ngRoute","ngAnimate",'ngMaterial','ui.router']).factory(
		'viewService',
		[
				"$http",
				function($http) {
					function loadFromServer(type, params, callback, isArray) {
						
						var paramString = "";
						for (name in params) {

							paramString = paramString + name + "=" + params[name] + "&";
						}

						paramString = paramString.length > 0 ? ("?" + paramString.slice(0,
								-1)) : "";

						return doLoad(type, paramString, callback, isArray);
					}

					function doLoad(type, paramString, callback, isArray) {
						
						var retval = isArray ? [] : {};
						
						function callbackWrapper(item){
							
							callback && item ? callback(item):null;
							angular.copy(item,retval);

						}
						
						$http.get("/view/" + type + paramString, {
							"responseType" : "json"
						}).success(callbackWrapper);
						
						return retval;
					}
					
					var textCache = {};

					var service = {

						load : function(type, id, callback) {

							return doLoad(type, "/" + id, callback);
						},

						view : function(type, params, callback) {

							var isArray = false;
							
							if(params){
								isArray = params.isArray;
								delete params.isArray;
							}

							return loadFromServer(type, params, callback,isArray);
						},

						list : function(type, callback) {

							return doLoad(type + "-list", "", callback, true);
						},

						post : function(type, payload, callback) {
							return $http.post("/view/" + type, payload).success(callback);
						},
						
						text : function(name) {
							var textHolder = textCache[name] = textCache.hasOwnProperty(name) ? textCache[name] : {};
							
							if(!textHolder.text){
								
								this.view("text",{name:name}, function(text){
									textHolder.text = text;
								});
							}
							
							return textHolder;
						}
					};
					return service;
				} ]);

mainApp.run([ '$rootScope', '$state', '$stateParams', '$mdDialog', 'viewService',
		function($rootScope, $state, $stateParams, $mdDialog, viewService) {

			$rootScope.$state = $state;
			$rootScope.$stateParams = $stateParams;

			$rootScope.closeDialog = function(){
				
				$mdDialog.hide();
			};
			
			$rootScope.showReports = function(results, result) {

				$mdDialog.show({
					templateUrl : '/results/reports.html',
					controller : "ReportsController",
					locals : {
						reportsData : {
							results : results,
							result : result
						}
					}
				});

			};
			
			$rootScope.showContactForm = function(title, recipient){
				
				$mdDialog.show({
					
					templateUrl : "/common/contact-dialog.html",
					clickOutsideToClose:false,
					locals : {title:title, recipient:recipient},
					controller: ['$scope',"title","recipient", function($scope, title, recipient) { 
						    $scope.title = title;
						    $scope.recipient = recipient;
						    $scope.sender = "";
						    $scope.text = "";
						    
						    $scope.send = function(){
						    	
						    	viewService.post("submit-contact",{
						    		recipient:$scope.recipient,
						    		sender:$scope.sender,
						    		text:$scope.text
						    	});
						    	
						    	$mdDialog.hide();
						    	
						    }
						    
						  }]
					
				});
			};
		} ]);

mainApp.config(['$stateProvider', '$urlRouterProvider','$locationProvider',
	             function ($stateProvider,   $urlRouterProvider, $locationProvider){
	
	
	$stateProvider.state("home", {
    url: "/",
    templateUrl: '/indexContents.html'

  })
  .state("rules", {
  	url:"/rules",
  	templateUrl : "/rules/rules.html"
  })
  .state("contact", {
  	url:"/contact",
  	templateUrl : "/contact/contact.html"
  })
  
  
  ;
	
$locationProvider.html5Mode(true);
}]);

mainApp.filter("htmlify", ["$sce", function($sce){return function(text){
	
	return text ?  $sce.trustAsHtml(text.replace(/^<p>/,"").replace(/<\/p>$/,"")) : "";
};}]);

mainApp.filter("lineBreaks", [function(){return function(text){
	return text ?  text.replace(/\n/g, "<br/>") : "";
};}]);

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

mainApp.directive('cqlText', ['htmlifyFilter','viewService',function(htmlify,viewService) {
    return {
     restrict: 'A',
     scope:{},
 
      link: function(scope, element, attrs){
    	  
    	  scope.$watch(attrs.cqlText, function(name){
    		  if(name){
        		  scope.text = viewService.text(name); 
    		  }

    	  });
    	  
    	  scope.$watch("text.text", function(text){
    		  
    		  if(text){
    			  element.html(htmlify(text.replace(/\\\"/g,"\"").replace(/\\n/g,"").replace(/^\"/,"").replace(/\"$/,"")));
    		  }
    		  
    	  });
    	  
    	 
      }
    };
  }]);

mainApp.directive('cqlResults', function() {
    return {
    	scope:{results:"="},
    	restrict:'E',
    	templateUrl:'/results/results-table-content.html'
    	
    };
  });

mainApp.directive('cqlSeasons', ["viewService",function(viewService) {
    return {
    	scope:{season:"="},
    	restrict:'E',
    	replace:true,
    	link: function(scope, element, attrs){
    		scope.seasons = viewService.list("season-views");
    		scope.labelStyle = attrs.hasOwnProperty("hidelabel") ? "display:none;" : "";
    		scope.selectStyle = attrs.hasOwnProperty("toolstyle") ? "background:transparent;border:none;":"";
     	},
    	templateUrl:'/common/season-dropdown.html'
    	
    };
  }]);


mainApp.directive('cqlDialog', function() {
	  return {
	    restrict: 'E',
	    scope: {
	      show: '='
	    },
	    replace: true, // Replace with the template below
	    transclude: true, // we want to insert custom content inside the directive
	    link: function(scope, element, attrs) {
	      scope.hideModal = function() {
	        scope.show = false;
	      };
	      scope.popup='';
	      scope.$watch("show", function(show){
	    	  scope.popup = show ? "popup" : "popdown"});
	    },
	    template: "<div ng-class='popup' class='modal'>" +
	    		"  <div  class='show-thin nofade popup-container'>" +
	    		" <div class='x-button'><a href='' ng-click='hideModal()'></a></div>" +
	    		"		<div style='height:98%;' ng-transclude></div>" +
	    		"	</div>	</div>"
	  };
	});



mainApp.controller('MainController', [ '$scope', '$interval', 'viewService', '$mdSidenav','$mdMedia','$mdDialog','$rootScope',
		function($scope, $interval, viewService, $mdSidenav, $mdMedia, $mdDialog, $rootScope) {

		  $scope.global = viewService.view("globaldata");
			
		  $scope.toggleRight = function() {
		    $mdSidenav('right').toggle();
		  };
		  
		  $scope.toggleLeft = function() {
		    $mdSidenav('left').toggle();
		  };
		  
		  $scope.closeLeft = function() {
			    $mdSidenav('left').close();
			  };
			  


		} ]);
