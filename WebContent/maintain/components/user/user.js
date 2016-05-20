maintainApp.controller('UserListCtrl', getCommonParams(makeListFn("user")));

maintainApp.controller('UserDetailCtrl', getCommonParams(makeUpdateFn("user")));
