 //控制层 
app.controller('payController' ,function($scope,$controller,$location   ,payService){
	
	$controller('baseController',{$scope:$scope});//继承
	
	$scope.createNative=function () {
		payService.createNative().success(function (response) {
			$scope.out_trade_no=response.out_trade_no;
			$scope.total_fee=(response.total_fee/100).toFixed(2);
			console.log(response.code_url);
			new QRious({
				element: document.getElementById('qrious'),
				size: 250,
				value: response.code_url,
				level:"H"
			})

			$scope.queryPayStatus();
		})
	}

	//查询支付状态
	$scope.queryPayStatus=function () {
		payService.queryPayStatus($scope.out_trade_no).success(function (response) {
			if (response.success){
				location.href="paysuccess.html#?totalFee="+$scope.total_fee;
			} else {
				if(response.message=="timeout"){
					$scope.createNative();
				}
				location.href="payfail.html";
			}
		})

	}
	//定义接收支付金额的方法
	$scope.getMoney=function () {
		$scope.totalFee =$location.search()["totalFee"];
		console.log($scope.totalFee);
	}

    
});	
