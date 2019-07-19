app.controller("brandController",function ($scope,$controller,brandService) {

    //控制器继承 $controller
    //参数一：继承的父控制器名称 参数二：固定写法：共享$scope
    $controller("baseController",{$scope:$scope});


    $scope.searchEntity={};  //解决初始请求参数为空的问题
    $scope.reloadList=function () {
        brandService.findPage($scope.paginationConf.currentPage,$scope.paginationConf.itemsPerPage,$scope.searchEntity).success(
            function (response) {
                $scope.list=response.rows;
                $scope.paginationConf.totalItems=response.total;
            }
        )
    };
    $scope.save=function () {
        var method=null;
        if ($scope.entity.id!=null){
            method=brandService.update($scope.entity);
        } else {
            method=brandService.add($scope.entity);
        }
        method.success(
            function (response) {
                if (response.success){
                    $scope.reloadList();
                } else {
                    alert(response.message);
                }
            }
        )
    };
    $scope.findOne=function (id) {
        brandService.findOne(id).success(
            function (response) {
                $scope.entity=response;
            }
        )
    };


    $scope.dele=function () {
        if (confirm("确定删除?")){
            brandService.dele($scope.selectIds).success(
                function (responce) {
                    if (responce.success){
                        $scope.reloadList();
                        $scope.selectIds=[];
                    } else {
                        alert(responce.message);
                    }
                }
            )
        }

    }


});