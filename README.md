# cordova-qdc-alipay
支付宝APP支付cordova,ionic插件(Android版，Ios版)

* 2015.07.01 支付宝Android集成，初步完成

# 1. Android客户端安装
开发工程下执行以下命令导入本插件：

	$ ionic plugin add https://github.com/mrwutong/cordova-qdc-alipay.git

已安装插件查看：

	$ionic plugin list


执行以下命令删本插件：

	# 【com.qdc.plugins.alipay】是插件ID，不是插件文件夹名
	$ionic plugin rm com.qdc.plugins.alipay

## 1.1 Android开发环境导入--Eclipse
导入路径：开发工程->platform->android

## 1.2 IOS开发环境导入--Xcode
导入路径：开发工程->platform->ios

确认没有编译错误。

## 1.3 JS调用说明

* 事先前调用后台支付宝API生成订单数据及签名数据
* 调用plugin的JS方法【alipay.payment】进行支付

```js
	**alipay.payment(json, cb_success, cb_failure);**
	# 参数说明：格式为JSON格式
	# cb_success:调用成功回调方法
	# cb_failure:调用失败回调方法
	{
	partner: 合作者身份ID
	private_key: 商户私钥
	notify_url: notify_url
	seller_id: 卖家支付宝账号
	subject: 商品名称
	body: 商品详情
	total_fee: 总金额
	out_trade_no:商户网站唯一订单号
	}
	注：订单总金额，只能可为小数，单位为【元】。
```

