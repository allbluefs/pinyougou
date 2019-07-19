app.controller("indexController",function ($scope,$controller,contentService) {
    //控制器继承 $controller
    //参数一：继承的父控制器名称 参数二：固定写法：共享$scope
    $controller("baseController",{$scope:$scope});

    $scope.contentList=[];
    $scope.findByCategoryId=function (categoryId) {
        contentService.findByCategoryId(categoryId).success(
            function (response) {
                $scope.contentList[categoryId]=response;
            }
        )
    }

    $scope.search=function () {
        if ($scope.keywords!=""){
            location.href="http://search.pinyougou.com/search.html#?keywords="+$scope.keywords;
        } else{
            location.href="http://search.pinyougou.com/search.html";
        }
    }
});