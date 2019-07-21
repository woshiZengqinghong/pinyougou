var app = new Vue({
    el: "#app",
    data: {
        pages:15,
        pageNo:1,
        list:[],
        entity:{},
        smsCode:"",
        loginName:"",//验证码
        repwd:"",//确认密码
        ids:[],
        searchEntity:{}
    },
    methods: {
        getName:function () {
            axios.get("/login/name.shtml").then(function (response) {
                app.loginName=response.data;
            }).catch(function (error) {
                console.log("1231312131321");
            });

        },
        createSmsCode:function () {
            axios.post("/user/sendCode.shtml?phone="+this.entity.phone).then(function (response) {
                if(response.data.success){
                    alert(response.data.message);//显示数据
                }else{
                    //发送失败
                    alert(response.data.message);//
                }
            }).catch(function (error) {
                console.log("1231312131321");
            })
        },
        formSubmit:function () {
            var _this=this;
            this.$validator.validate().then(
                function (result) {
                    if (result) {
                        console.log(_this);
                        axios.post("/user/add/"+app.smsCode+".shtml",this.entity).then(function (response) {
                            if (response.data.successs) {
                                //跳转其用户后台首页
                                window.location.href="home-index.html";
                            }else{
                                this.$validator.errors.add(response.data.errorsList);
                            }

                        }).catch(function (error) {
                            console.log("1231312131321");
                        })
                    }
                }
            )
        }



    },
    //钩子函数 初始化了事件和
    created: function () {
        this.getName();
    }
})
