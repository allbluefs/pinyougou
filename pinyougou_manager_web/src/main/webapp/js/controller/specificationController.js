app.controller("specificationController",function ($scope,$controller,specificationService) {

    //控制器继承 $controller
    //参数一：继承的父控制器名称 参数二：固定写法：共享$scope
    $controller("baseController",{$scope:$scope});


    $scope.searchEntity={};  //解决初始请求参数为空的问题
    $scope.reloadList=function () {
        specificationService.findPage($scope.paginationConf.currentPage,$scope.paginationConf.itemsPerPage,$scope.searchEntity).success(
            function (response) {
                $scope.list=response.rows;
                $scope.paginationConf.totalItems=response.total;
            }
        )
    };

    //初始化entity对象
    $scope.entity={
        specification:{},
        specificationOptions:[]
    };
    $scope.addRow=function () {
        $scope.entity.specificationOptions.push({});
    };
    $scope.deleRow=function (index) {
        $scope.entity.specificationOptions.splice(index,1);
    };
    $scope.save=function () {
        var method=null;
        if ($scope.entity.specification.id!=null){
            method=specificationService.update($scope.entity);
        } else {
            method=specificationService.add($scope.entity);
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
        specificationService.findOne(id).success(
            function (response) {
                $scope.entity=response;
            }
        )
    };

    $scope.dele=function () {
        if (confirm("确定删除?")){
            specificationService.dele($scope.selectIds).success(
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