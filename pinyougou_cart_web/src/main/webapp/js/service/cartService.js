//服务层
app.service('cartService',function($http){

    this.findCartList=function () {
        return $http.get('cart/findCartList.do')
    }
    this.addItemToCartList=function (itemId,num) {
        return $http.get('cart/addGoodsToCartList.do?itemId='+itemId+'&num='+num);
    }



});
