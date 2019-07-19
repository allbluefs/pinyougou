app.controller("indexController",function ($scope,$controller,loginService) {
    //控制器继承 $controller
    //参数一：继承的父控制器名称 参数二：固定写法：共享$scope
    $controller("baseController",{$scope:$scope});

    $scope.getLoginName=function () {
        loginService.getLoginName().success(function (response) {
            $scope.loginName=response.loginName;
        })
    }
});