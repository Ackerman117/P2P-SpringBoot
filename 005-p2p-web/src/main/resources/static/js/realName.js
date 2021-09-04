
//同意实名认证协议
$(function() {
	$("#agree").click(function(){
		var ischeck = document.getElementById("agree").checked;
		if (ischeck) {
			$("#btnRegist").attr("disabled", false);
			$("#btnRegist").removeClass("fail");
		} else {
			$("#btnRegist").attr("disabled","disabled");
			$("#btnRegist").addClass("fail");
		}
	});

	// 对手机号输入框进行非空和格式验证
	$("#phone").on("blur",function () {
		var phone = $.trim($("#phone").val());
		if ("" == phone) {
			showError("phone","请输入手机号")
		} else if (!/^1[1-9]\d{9}$/.test(phone)) {
			showError("phone","手机号格式不正确");
		}else {
			showSuccess("phone");
		}
	});


	<!-- 真实姓名验证 -->
	$("#realName").on("blur",function () {
		var realName = $.trim($("#realName").val());
		if ("" == realName) {
			showError("realName","请输入真实姓名");
		} else if (!/^[\u4e00-\u9fa5]{0,}$/.test(realName)) {
			showError("realName","只能是中文名称");
		} else {
			showSuccess("realName");
		}
	});

	<!-- 身份证号码验证 -->
	$("#idCard").on("blur",function () {
		var idCard = $.trim($("#idCard").val());
		if ("" == idCard) {
			showError("idCard","身份证号码不能为空");
		} else if (!/(^\d{15}$)|(^\d{18}$)|(^\d{17}(\d|X|x)$)/.test(idCard)) {
			showError("idCard","请输入正确的身份证号")
		} else {
			showSuccess("idCard");
		}
	});


	// 实现获取验证码
	// 倒计时按钮
	$("#messageCodeBtn").on("click", function () {

		if (!$("#messageCodeBtn").hasClass("on")) {

			$("#phone").blur();

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
									$("#messageCodeBtn").html((d.s == "00" ? "60" : d.s) + "秒重新获取");
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


	<!--认证按钮的单击事件-->
	$("#btnRegist").on("click",function () {
		$("#phone").blur();
		$("#realName").blur();
		$("#idCard").blur();
		$("#messageCode").blur();

		// 如果没有错误信息则发出请求
		var errorText =   $("div[id$='Err']").text();
		if ("" == errorText) {
			// 发出请求
			$.ajax({
				url: "/p2p/loan/realName",
				type: "post",
				data: {
					"phone":$.trim($("#phone").val()),
					"realName":$.trim($("#realName").val()),
					"idCard":$.trim($("#idCard").val()),
					"messageCode":$.trim($("#messageCode").val())
				},
				success:function (data) {
					if (data.code == 1) {
						window.location.href = "/p2p/index"
					} else {
						$("#messageCode").val("");
						showError("messageCode",data.message);
					}
				},
				error:function () {
					$("#messageCode").val("");
					showError("messageCode","系统异常");
				}

			});
		}

	});


});
//打开注册协议弹层
function alertBox(maskid,bosid){
	$("#"+maskid).show();
	$("#"+bosid).show();
}
//关闭注册协议弹层
function closeBox(maskid,bosid){
	$("#"+maskid).hide();
	$("#"+bosid).hide();
}

//错误提示
function showError(id,msg) {
	$("#"+id+"Ok").hide();
	$("#"+id+"Err").html("<i></i><p>"+msg+"</p>");
	$("#"+id+"Err").show();
	$("#"+id).addClass("input-red");
}
//错误隐藏
function hideError(id) {
	$("#"+id+"Err").hide();
	$("#"+id+"Err").html("");
	$("#"+id).removeClass("input-red");
}
//显示成功
function showSuccess(id) {
	$("#"+id+"Err").hide();
	$("#"+id+"Err").html("");
	$("#"+id+"Ok").show();
	$("#"+id).removeClass("input-red");
}