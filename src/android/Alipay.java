package com.qdc.plugins.alipay;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import org.apache.cordova.CallbackContext;
import org.apache.cordova.CordovaPlugin;
import org.apache.cordova.LOG;
import org.apache.cordova.PluginResult;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.alipay.sdk.app.PayTask;

/**
 * 支付宝支付插件
 * 
 * @author NCIT
 * 
 */
public class Alipay extends CordovaPlugin {
  /** JS回调接口对象 */
  public static CallbackContext cbContext = null;

  /** LOG TAG */
  private static final String LOG_TAG = Alipay.class.getSimpleName();

  /**
   * 插件主入口
   */
  @Override
  public boolean execute(String action, final JSONArray args,
      CallbackContext callbackContext) throws JSONException {
    LOG.d(LOG_TAG, "Alipay#execute");

    boolean ret = false;

    if ("payment".equalsIgnoreCase(action)) {
      LOG.d(LOG_TAG, "Alipay#payment.start");

      cbContext = callbackContext;

      PluginResult pluginResult = new PluginResult(
          PluginResult.Status.NO_RESULT);
      pluginResult.setKeepCallback(true);
      callbackContext.sendPluginResult(pluginResult);

      // 参数检查
      if (args.length() != 1) {
        LOG.e(LOG_TAG, "args is empty", new NullPointerException());
        ret = false;
        PluginResult result = new PluginResult(
            PluginResult.Status.ERROR, "args is empty");
        result.setKeepCallback(true);
        cbContext.sendPluginResult(result);
        return ret;
      }

      JSONObject jsonObj = args.getJSONObject(0);

      final String partner = jsonObj.getString("partner");
      if (partner == null || "".equals(partner)) {
        LOG.e(LOG_TAG, "partner is empty", new NullPointerException());
        ret = false;
        PluginResult result = new PluginResult(
            PluginResult.Status.ERROR, "partner is empty");
        result.setKeepCallback(true);
        cbContext.sendPluginResult(result);
        return ret;
      }

      final String private_key = jsonObj.getString("private_key");
      if (private_key == null || "".equals(private_key)) {
        LOG.e(LOG_TAG, "private_key is empty",
            new NullPointerException());
        ret = false;
        PluginResult result = new PluginResult(
            PluginResult.Status.ERROR, "private_key is empty");
        result.setKeepCallback(true);
        cbContext.sendPluginResult(result);
        return ret;
      }

      final String notifyUrl = jsonObj.getString("notify_url");
      if (notifyUrl == null || "".equals(notifyUrl)) {
        LOG.e(LOG_TAG, "notify_url is empty",
            new NullPointerException());
        ret = false;
        PluginResult result = new PluginResult(
            PluginResult.Status.ERROR, "notify_url is empty");
        result.setKeepCallback(true);
        cbContext.sendPluginResult(result);
        return ret;
      }

      final String sellerId = jsonObj.getString("seller_id");
      if (sellerId == null || "".equals(sellerId)) {
        LOG.e(LOG_TAG, "seller_id is empty", new NullPointerException());
        ret = false;
        PluginResult result = new PluginResult(
            PluginResult.Status.ERROR, "seller_id is empty");
        result.setKeepCallback(true);
        cbContext.sendPluginResult(result);
        return ret;
      }

      final String subject = jsonObj.getString("subject");
      if (subject == null || "".equals(subject)) {
        LOG.e(LOG_TAG, "subject is empty", new NullPointerException());
        ret = false;
        PluginResult result = new PluginResult(
            PluginResult.Status.ERROR, "subject is empty");
        result.setKeepCallback(true);
        cbContext.sendPluginResult(result);
        return ret;
      }

      final String body = jsonObj.getString("body");
      if (body == null || "".equals(body)) {
        LOG.e(LOG_TAG, "body is empty", new NullPointerException());
        ret = false;
        PluginResult result = new PluginResult(
            PluginResult.Status.ERROR, "body is empty");
        result.setKeepCallback(true);
        cbContext.sendPluginResult(result);
        return ret;
      }

      final String totalFee = jsonObj.getString("total_fee");
      if (totalFee == null || "".equals(totalFee)) {
        LOG.e(LOG_TAG, "total_fee is empty", new NullPointerException());
        ret = false;
        PluginResult result = new PluginResult(
            PluginResult.Status.ERROR, "total_fee is empty");
        result.setKeepCallback(true);
        cbContext.sendPluginResult(result);
        return ret;
      }

      final String outTradeNo = jsonObj.getString("out_trade_no");
      if (outTradeNo == null || "".equals(outTradeNo)) {
        LOG.e(LOG_TAG, "out_trade_no is empty",
            new NullPointerException());
        ret = false;
        PluginResult result = new PluginResult(
            PluginResult.Status.ERROR, "out_trade_no is empty");
        result.setKeepCallback(true);
        cbContext.sendPluginResult(result);
        return ret;
      }

      String orderInfo = getOrderInfo(partner, sellerId, subject, body,
          totalFee, outTradeNo, notifyUrl);
      String sign = SignUtils.sign(orderInfo, private_key);
      try {
        // 仅需对sign 做URL编码
        sign = URLEncoder.encode(sign, "UTF-8");
      } catch (UnsupportedEncodingException e) {
        LOG.e(LOG_TAG, e.getMessage(), e);
        ret = false;
        PluginResult result = new PluginResult(
            PluginResult.Status.ERROR, "sign failure");
        result.setKeepCallback(true);
        cbContext.sendPluginResult(result);
        return ret;
      }
      final String payInfo = orderInfo.concat("&sign=\"").concat(sign)
          .concat("\"&sign_type=\"RSA\"");

      Runnable payRunnable = new Runnable() {

        @Override
        public void run() {
          // 构造PayTask 对象
          PayTask alipay = new PayTask(cordova.getActivity());

          // 查询终端设备是否存在支付宝认证账户
          boolean isExist = alipay.checkAccountIfExist();
          if (!isExist) {
            LOG.e(LOG_TAG, "alipay account is not exists",
                new IllegalStateException());
            PluginResult result = new PluginResult(
                PluginResult.Status.ERROR,
                "alipay account is not exists");
            result.setKeepCallback(true);
            cbContext.sendPluginResult(result);
            return;
          }
          
          // 调用支付接口
          String resultMsg = alipay.pay(payInfo);
          LOG.i(LOG_TAG, ">>>>>>>>>>支付回调通知>>>>>>>>>>>");
          LOG.i(LOG_TAG, resultMsg);
          LOG.i(LOG_TAG, "<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<");

          String[] resArr = resultMsg.split(";");
          JSONObject resJo = new JSONObject();
          for (String res : resArr) {
            String[] ress = res.split("=");
            String key = ress[0];
            String value = ress[1].substring(1, ress[1].length() - 1);
            try {
              resJo.put(key, value);
            } catch (JSONException e) {
              LOG.e(LOG_TAG, e.getMessage(), e);
            }
          }

          PluginResult result = new PluginResult(PluginResult.Status.OK,
              resJo.toString());
          result.setKeepCallback(true);
          cbContext.sendPluginResult(result);
        }
      };

      // 此处必须通过启动线程调起支付
      Thread payThread = new Thread(payRunnable);
      payThread.start();

      LOG.d(LOG_TAG, "Alipay#payment.end");
      return true;
    }

    return true;
  }

  /**
   * 创建订单信息
   */
  private String getOrderInfo(String partner, String sellerId,
      String subject, String body, String totalFee, String outTradeNo,
      String notifyUrl) {
    StringBuilder sb = new StringBuilder();
    // 合作者身份ID
    sb.append("partner=\"").append(partner).append("\"");
    // 卖家支付宝账号
    sb.append("&seller_id=\"").append(sellerId).append("\"");
    // 商户网站唯一订单号
    sb.append("&out_trade_no=\"").append(outTradeNo).append("\"");
    // 商品名称
    sb.append("&subject=\"").append(subject).append("\"");
    // 商品详情
    sb.append("&body=\"").append(body).append("\"");
    // 商品金额
    sb.append("&total_fee=\"").append(totalFee).append("\"");
    // 服务器异步通知页面路径
    sb.append("&notify_url=\"").append(notifyUrl).append("\"");
    // 接口名称， 固定值
    sb.append("&service=\"mobile.securitypay.pay\"");
    // 支付类型， 固定值
    sb.append("&payment_type=\"1\"");
    // 参数编码， 固定值
    sb.append("&_input_charset=\"utf-8\"");
    return sb.toString();
  }

}