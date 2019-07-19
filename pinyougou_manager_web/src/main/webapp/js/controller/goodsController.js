//控制层
app.controller('goodsController', function ($scope, $controller, goodsService, itemCatService) {

    $controller('baseController', {$scope: $scope});//继承

    //读取列表数据绑定到表单中  
    $scope.findAll = function () {
        goodsService.findAll().success(
            function (response) {
                $scope.list = response;
            }
        );
    }

    //分页
    $scope.findPage = function (page, rows) {
        goodsService.findPage(page, rows).success(
            function (response) {
                $scope.list = response.rows;
                $scope.paginationConf.totalItems = response.total;//更新总记录数
            }
        );
    }

    //查询实体
    $scope.findOne = function (id) {
        goodsService.findOne(id).success(
            function (response) {
                $scope.entity = response;
            }
        );
    }

    //保存
    $scope.save = function () {
        var serviceObject;//服务层对象
        if ($scope.entity.goods.id != null) {//如果有ID
            serviceObject = goodsService.update($scope.entity); //修改
        } else {
            $scope.entity.goodsDesc.introduction = editor.html();
            serviceObject = goodsService.add($scope.entity);//增加
        }
        serviceObject.success(
            function (response) {
                if (response.success) {
                    $scope.entity = {};
                    editor.html("");
                } else {
                    alert(response.message);
                }
            }
        );
    }


    //批量删除
    $scope.dele = function () {
        //获取选中的复选框
        goodsService.dele($scope.selectIds).success(
            function (response) {
                if (response.success) {
                    $scope.reloadList();//刷新列表
                }
            }
        );
    }

    $scope.searchEntity = {};//定义搜索对象

    //搜索
    $scope.search = function (page, rows) {
        goodsService.search(page, rows, $scope.searchEntity).success(
            function (response) {
                $scope.list = response.rows;
                $scope.paginationConf.totalItems = response.total;//更新总记录数
            }
        );
    }


    $scope.selectCategory1List = function () {
        itemCatService.findByParentId(0).success(
            function (response) {
                $scope.category1List = response;

            }
        )
    };


    //初始entity对象
    $scope.entity = {
        goods:{isEnableSpec:'1'},
        goodsDesc:{itemImages:[],specificationItems:[]},
        itemList:[]
    };



    /**
     * 判断规格名称对应的规格对象是否存在与勾选的规格列表中
     如果不存在
     新增规格对象到勾选的规格列表中

     如果存在
     判断是勾选还是取消勾选规格选择数据
     如果勾选
     在已存在的规格对象中规格选项列表中添加勾选的规格选项数据

     取消勾选
     在已存在的规格对象中规格选项列表中移除取消勾选的规格选项数据

     如果规格对象中规格选项列表中的规格选项数据全部移除
     这从勾选的规格列表中，移除该规格对象
     */

    $scope.updateSpecAttribute = function ($event, specName, specOptionName) {
        //判断规格名称对应的规格对象是否存在与勾选的规格列表中  [{"attributeName":"网络","attributeValue":["移动3G"]}]
        var specObj = $scope.getObjectByValue($scope.entity.goodsDesc.specificationItems, "attributeName", specName);

        if (specObj != null) {	// 如果存在
            // 判断是勾选还是取消勾选规格选择数据

            if ($event.target.checked) {// 如果勾选
                // 在已存在的规格对象中规格选项列表中添加勾选的规格选项数据
                specObj.attributeValue.push(specOptionName);
            } else {// 取消勾选
                // 在已存在的规格对象中规格选项列表中移除取消勾选的规格选项数据
                var index = specObj.attributeValue.indexOf(specOptionName);
                specObj.attributeValue.splice(index, 1);
                // 如果规格对象中规格选项列表中的规格选项数据全部移除
                if (specObj.attributeValue.length == 0) {
                    // 这从勾选的规格列表中，移除该规格对象
                    $scope.entity.goodsDesc.specificationItems.splice($scope.entity.goodsDesc.specificationItems.indexOf(specObj), 1);
                }
            }
        } else {// 如果不存在
            // 新增规格对象到勾选的规格列表中
            $scope.entity.goodsDesc.specificationItems.push({"attributeName":specName,"attributeValue":[specOptionName]});
        }
    };

    //构建sku列表 itemList
    $scope.creatItemList=function () {
        //初始化item对象  spec:{"机身内存":"16G","网络":"联通3G"}
        $scope.entity.itemList=[{spec:{},price:0,num : 99999,status:'1',isDefault:'0'}];// 初始
        // 勾选的规格结果集 [{"attributeName":"网络","attributeValue":["移动3G","联通3G"]},{"attributeName":"机身内存","attributeValue":["64G"]}]
        var specList=$scope.entity.goodsDesc.specificationItems;
        if (specList.length==0){
            $scope.entity.itemList=[];
        }
        for (var i=0;i<specList.length;i++){
            $scope.entity.itemList=addColumn($scope.entity.itemList,specList[i].attributeName,specList[i].attributeValue);
        }
    }
    addColumn=function (list,specName,specOptionName) {
            //定义新的sku列表
        var newList=[];
        for (var i=0;i<list.length;i++){
            var oldItem=list[i];
            for (var j=0;j<specOptionName.length;j++){
                var newItem=JSON.parse(JSON.stringify(oldItem));
                newItem.spec[specName]=specOptionName[j];
                newList.push(newItem);
            }
        }
        return newList;
    }

    //商品审核状态数组
    $scope.status = ['未审核','已审核','审核未通过','关闭'];
    //定义商品分类展示的数组
    $scope.itemCatArr=[];
    //优化商品列表分类展示
    $scope.selectAllCategory=function () {
        itemCatService.findAll().success(
            function (response) {
                for (var i=0;i<response.length;i++){
                    $scope.itemCatArr[response[i].id]=response[i].name;
                }
            }
        )
    };
    //上下架状态数组
    $scope.isMarketable = ['下架','上架'];

    $scope.updatesIsMarketable=function (isMarketable) {
        goodsService.updatesIsMarketable($scope.selectIds,isMarketable).success(
            function (response) {
                if (response.success){
                    $scope.reloadList();
                    $scope.selectIds=[];
                }else {
                    alert(response.message);
                }
            }
        )
    }
    //商品审核
    $scope.updateStatus=function (status) {
        goodsService.updateStatus($scope.selectIds,status).success(function (response) {
            if (response.success){
                //审核成功
                $scope.reloadList();
                $scope.selectIds=[];
            } else {
                alert(response.message);
            }
        })
    }

});	
