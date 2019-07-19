app.service("specificationService",function ($http) {

    this.findPage=function (pageNumber,pageSize,searchEntity) {
        return $http.post("../specification/findPage.do?pageNumber="+pageNumber+"&pageSize="+pageSize,searchEntity);
    };
    this.add=function (entity) {
        return $http.post("../specification/add.do",entity);
    };
    this.update=function (entity) {
        return $http.post("../specification/update.do",entity);
    };
    this.findOne=function (id) {
        return $http.get("../specification/findOne.do?id="+id);
    };
    this.dele=function (ids) {
        return $http.get("../specification/dele.do?ids="+ids);
    };
    this.selectSpecOptions=function () {
        return $http.get("../specification/selectSpecOptions.do?");
    }
});