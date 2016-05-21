maintainApp.controller("MassMailCtrl", ["$scope","entityService","$rootRouter", function($scope, entityService, $rootRouter){

	entityService.load("global",0, function(global){$scope.global=global})
	
	$scope.submit = function(subject,text,sender){
		
		entityService.save("mass-mail",{"subject":subject,"text":text, "sender":sender.alias},function(){
			$rootRouter.navigate(["Root", "Mail"])
		})
	}
}])