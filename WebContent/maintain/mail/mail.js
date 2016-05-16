maintainApp.controller("MassMailCtrl", ["$scope","entityService","$state", function($scope, entityService, $state){

	entityService.load("global",0, function(global){$scope.global=global})
	
	$scope.submit = function(subject,text,sender){
		
		entityService.save("mass-mail",{"subject":subject,"text":text, "sender":sender.alias},function(){
			$state.go("mail")
		})
	}
}])