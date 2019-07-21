var app = new Vue({
    el: "#app",
    data: {
       entity:{}
    },
    methods:{
        //商家申请的方法
        register:function () {
            axios.post('/seller/add.shtml',this.entity).then(
                function (response) {
                    //如果申请成功  跳转到登录的页面

                    if(response.data.success){
                        window.location.href="shoplogin.html";
                    }else{
                        alert(response.data.message);
                    }

                }
            )
        }
    },
    created:function () {

    }
});