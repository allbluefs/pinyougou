//����������������ַ���ΪHTML��ǩ
//ʹ�÷�ʽ��<em ng-bind-html="����ǩ������ | trustHtml"></em>
app.filter("trustHtml",function($sce){
	return function(data){
		return $sce.trustAsHtml(data);
	};
});