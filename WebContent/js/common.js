var b = document.documentElement;
  b.setAttribute('data-useragent',  navigator.userAgent);
  b.setAttribute('data-platform', navigator.platform );
  b.className += ((!!('ontouchstart' in window) || !!('onmsgesturechange' in window))?' touch':'');

	
 var COMMON = {
		 
		 configureGroupController : function(name,ctrl, $scope, viewService){
				
				var listName = name + "s"
				
				viewService.list(name, function(items){$scope[listName] = items})
				
				ctrl.setId = function(id){$scope.id = id}
				ctrl.watch = function(name, lstn){return $scope.$watch(name, lstn)}
				
				$scope.$watchGroup([listName, "id"], function(values){
					if(values[0] && values[1]){
						$scope[name] = values[0].filter(function(v){return v.id == values[1]}).pop()
					}
				})
				
			},
		 
		 configureItemController : function(name,ctrl, $scope){
				var listName = name + "s"
				
				var derefs = []
				ctrl.$routerOnActivate = function(next, previous) {
					ctrl[listName].setId(next.params.id);
				}
				
				ctrl.$onInit = function(){
					derefs.push(ctrl[listName].watch(name, 
							function(item){
						$scope[name] = item
						}))
				}
				
				ctrl.$onDelete = function(){
					derefs.forEach(function(i){i()})
					derefs = []
				}
			}
		 
 } 
  

	

