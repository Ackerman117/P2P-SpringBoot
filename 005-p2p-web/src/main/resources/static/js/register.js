//错误提示
function showError(id, msg) {
    $("#" + id + "Ok").hide();
    $("#" + id + "Err").html("<i></i><p>" + msg + "</p>");
    $("#" + id + "Err").show();
    $("#" + id).addClass("input-red");
}

//错误隐藏
function hideError(id) {
    $("#" + id + "Err").hide();
    $("#" + id + "Err").html("");
    $("#" + id).removeClass("input-red");
}

//显示成功
function showSuccess(id) {
    $("#" + id + "Err").hide();
    $("#" + id + "Err").html("");
    $("#" + id + "Ok").show();
    $("#" + id).removeClass("input-red");
}


//打开注册协议弹层
function alertBox(maskid, bosid) {
    $("#" + maskid).show();
    $("#" + bosid).show();
}

//关闭注册协议弹层
function closeBox(maskid, bosid) {
    $("#" + maskid).hide();
    $("#" + bosid).hide();
}

//注册协议确认
$(function () {
    $("#agree").click(function () {
        var ischeck = document.getElementById("agree").checked;
        if (ischeck) {
            $("#btnRegist").attr("disabled", false);
            $("#btnRegist").removeClass("fail");
        } else {
            $("#btnRegist").attr("disabled", "disabled");
            $("#btnRegist").addClass("fail");
        }
    });


    // 页面加载完成，手机号输入框自动获取焦点
    $(function () {
        $("#phone").focus();
    });


    // 给手机号输入框添加一个失去焦点的事件
    $("#phone").on("blur", function () {
        var phone = $.trim($("#phone").val());
        if (phone == "") {
            showError("phone", "对不起，手机号不能为空");
        } else if (!(/^1[1-9]\d{9}$/.test(phone))) {
            showError("phone", "手机号码不正确")
        } else {
            // 验证手机号是否存在
            /*
                1.获取手机号，然后去后台验证
                2.手机号存在，则提示用户手机号已被注册
                3.手机号不存在，则可以使用并调用showSuccess()函数
             */
            $.ajax({
                url: "/p2p/loan/checkPhone",
                type: "get",
                data: {
                    "phone": phone
                },
                success: function (data) {
                    if (data.code == "1") {
                        showSuccess("phone")
                    } else {
                        showError("phone", data.message)
                    }
                },
                error: function () {
                    showError("phone", "系统繁忙，请稍后再试。")
                }
            });
        }
    });


    // 验证密码
    $("#loginPassword").on("blur", function () {
        // 获取密码
        var loginPassword = $.trim($("#loginPassword").val());
        if ("" == loginPassword) {
            showError("loginPassword", "密码不能为空");
        } else if (!/^[0-9a-zA-Z]+$/.test(loginPassword)) {
            showError("loginPassword", "密码只可使用数字和大小写英文字母");
        } else if (!/^(([a-zA-Z]+[0-9]+)|([0-9]+[a-zA-Z]+))[a-zA-Z0-9]*/.test(loginPassword)) {
            showError("loginPassword", "密码必须包含英文和数字");
        } else if (loginPassword.length < 6 || loginPassword.length > 20) {
            showError("loginPassword", "密码长度应为6到20位");
        } else {
            showSuccess("loginPassword");
        }
    });

    // 注册
    $("#btnRegist").on("click", function () {
        $("#phone").blur();
        $("#loginPassword").blur();
        var errorText = $("div[id$='Err']").text();
        // 1.点击注册后先获取用户名和密码
        var phone = $.trim($("#phone").val());
        var loginPassword = $.trim($("#loginPassword").val());
        var messageCode = $.trim($("#messageCode").val());
        if ("" == errorText) {

            // 2.加长密码
            $("#loginPassword").val($.md5(loginPassword)); //其实就是md5加密  位于文件/js/jQuery.md5.js中

            // 4.代码用户名和密码箱后台发出请求
            if ("" == errorText) {
                // 给密码输入框重新赋值
                $("#loginPassword").val($.md5(loginPassword));
                $.ajax({
                    url: "/p2p/loan/register",
                    type: "post",
                    data: {
                        "phone": phone,
                        "loginPassword": $.md5(loginPassword),
                        "messageCode": messageCode
                    },
                    success: function (data) {
                        if (data.code == 1) {
                            window.location.href = "/p2p/user/realName";
                        } else {
                            showError("messageCode", data.message)
                        }
                    },
                    error: function () {
                        showError("messageCode", "系统繁忙，请稍后重试")
                    }


                });
            }

        }
    });


    // 实现获取验证码
    // 倒计时按钮
    $("#messageCodeBtn").on("click", function () {

        if (!$("#messageCodeBtn").hasClass("on")) {

            $("#phone").blur();
            $("#loginPassword").blur();

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

});
