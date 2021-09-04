var referrer = "";//登录后返回页面
referrer = document.referrer;
if (!referrer) {
	try {
		if (window.opener) {                
			// IE下如果跨域则抛出权限异常，Safari和Chrome下window.opener.location没有任何属性              
			referrer = window.opener.location.href;
		}  
	} catch (e) {
	}
}

//按键盘Enter键即可登录
$(document).keyup(function(event){
	if(event.keyCode == 13){
		login();
	}
});


$(function () {
	// 实现获取验证码
	// 倒计时按钮
	$("#messageCodeBtn").on("click", function () {

		if (!$("#messageCodeBtn").hasClass("on")) {

			var phone = $.trim($("#phone").val());

			// 获取错误提示信息
			var errorText = $("div[id$='Err']").text();

			if ("" == errorText) {
				// 开始验证
				$.ajax({
					url: "/p2p/user/messageCode",
					type: "post",
					data: {
						"phone": phone
					},
					success: function (data) {
						if (data.code == 1) {
							$.leftTime(60, function (d) {
								if (d.status) {
									// 倒计时
									//1. 先把按钮设置为不可用
									$("#messageCodeBtn").addClass("on");
									//2. 把修改按钮的文本内容
									$("#messageCodeBtn").html((d.s == "00" ? "60" : d.s) + "秒后获取");
								} else {
									// 倒计时结束
									// 修改按钮样式，设置为可用
									$("#messageCodeBtn").removeClass("on");
									$("#messageCodeBtn").html("获取验证码");
								}
							})
						} else {
							showError("messageCode", "发送验证码失败");
						}
					},
					error: function () {
						showError("messageCode", "发送验证码失败，请稍后再试");
					}
				});
			}
		}
	});
})
