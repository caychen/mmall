* 1、遗留问题：
使用swagger2之后，在访问http://localhost:8080/swagger-ui.html之后会弹出一个框，提示‘Unable to infer base url. This is common when using dynamic servlet registration or when the API is behind an API Gateway. The base url is the root of where all the swagger resources are served. For e.g. if the api is available at http://example.org/api/v2/api-docs then the base url is http://example.org/api/. Please enter the location manually: ’，
查遍网站都没找到解决方案。

* 2、测试时无法使用公网进行支付宝回调，所以改成使用线程轮询来查询订单状态，
如果是交易成功，则将数据插入到pay_info表中，并停止线程，而交易关闭则只是停止线程，不进行数据插入，