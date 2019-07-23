app.controller("baseController",function ($scope) {
    //定义分页对象，分页配置
    $scope.paginationConf = {
        currentPage:1,  				//当前页
        totalItems:10,					//总记录数
        itemsPerPage:10,				//每页记录数
        perPageOptions:[10,20,30,40,50], //分页选项，下拉选择一页多少条记录
        onChange:function(){			//页面变更后触发的方法
            $scope.reloadList();		//启动就会调用分页组件
            $scope.selectIds=[];
        }
    };
    //重新加载列表 数据
    $scope.reloadList=function(){
        //切换页码
        $scope.search( $scope.paginationConf.currentPage, $scope.paginationConf.itemsPerPage);
    };

    $scope.selectIds=[];
    $scope.updateSelection=function ($event,id) {
        if ($event.target.checked){
            $scope.selectIds.push(id);
        }
        else {
            //取消勾选
            //获取元素索引
            var index=$scope.selectIds.indexOf(id);
            $scope.selectIds.splice(index,1);
        }
    };

    //解析json格式字符串，获取字符串中的对象，基于对象属性名获取属性值，然后将属性值以逗号格式拼接
    //例如：[{"id":27,"text":"网络"},{"id":32,"text":"机身内存"}]  [{"id":27,"name":"网络"},{"id":32,"name":"机身内存"}]
    $scope.parseJsonString=function (jsonString,key) {
        //解析json格式字符串
        var jsonArr=JSON.parse(jsonString);
        var value="";
        for (var i=0;i<jsonArr.length;i++){
            if (i==0){
                value+=jsonArr[i][key];
            } else {
                value+=","+jsonArr[i][key];
            }
        }
        return value;
    }

});