 //控制层 
app.controller('orderController' ,function($scope,$controller   ,orderService,addressService,cartService){
	
	$controller('baseController',{$scope:$scope});//继承
	
	$scope.findByUserId=function () {
		addressService.findByUserId().success(function (response) {
			$scope.addressList=response;
			//判断是否是默认地址
			for(var i=0;i<$scope.addressList.length;i++){
				if($scope.addressList[i].isDefault==1){
					$scope.address=$scope.addressList[i];
					break;
				}
			}
			//没有设置默认地址时，将第一个地址设为默认地址
			if($scope.address == null){
				$scope.address=$scope.addressList[0];
			}
		})
	}
	//定义寄送地址
	$scope.address = null;

	//判断地址是否需要选择
	$scope.isSelectedAddr=function (addr) {
		if($scope.address==addr){
			return true;
		}else{
			return false;
		}
	}

	$scope.updateAddr=function (addr) {
		$scope.address=addr;
	}

	//查询购物车列表
	$scope.findCartList=function () {
		cartService.findCartList().success(function (response) {
			$scope.cartList=response;
			//商品数量和金额统计
			sum();
		})
	}
	//初始化订单对象，默认支付方式为：在线支付
	$scope.order={paymentType:"1"};

	//切换支付方式
	$scope.updatePaymentType=function (type) {
		$scope.order.paymentType=type;
	}

	//统计商品总数量和总金额
	sum=function () {
		$scope.totalNum=0;
		$scope.totalMoney=0.00;
		for(var i=0;i< $scope.cartList.length;i++){
			//获取购物车对象
			var cart =  $scope.cartList[i];
			//获取商品列表
			var orderItemList = cart.orderItemList;
			for(var j=0;j<orderItemList.length;j++){
				$scope.totalNum+=orderItemList[j].num;
				$scope.totalMoney+=orderItemList[j].totalFee;
			}
		}

	}

	//订单保存
	$scope.saveOrder=function () {
		// `receiver_area_name` varchar(100) COLLATE utf8_bin DEFAULT NULL COMMENT '收货人地区名称(省，市，县)街道',
		//     `receiver_mobile` varchar(12) COLLATE utf8_bin DEFAULT NULL COMMENT '收货人手机',
		//     `receiver` varchar(50) COLLATE utf8_bin DEFAULT NULL COMMENT '收货人',

		$scope.order.receiverAreaName = $scope.address.address;
		$scope.order.receiverMobile = $scope.address.mobile;
		$scope.order.receiver = $scope.address.contact;
		orderService.add($scope.order).success(function (response) {
			if(response.success) {
				//保存订单成功，跳转支付页面
				location.href = "pay.html";
			}else{
				alert(response.message);
			}
		})
	}
    
});	
