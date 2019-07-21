var app = new Vue({
    el: '#app',
    data:{
        num:1,
        specificationItems:JSON.parse(JSON.stringify(skuList[0].spec)), //默认选中对象规格 因为会被改变  所以深克隆
        sku:skuList[0]//数组第一个元素就是sku的数据展示  只展示 不改变 不需克隆
    },

    methods: {
        /**
         * 添加购物车
         * @param itemId
         * @param num
         */
        addGoodsToCartList:function (itemId,num) {
            axios.get('http://localhost:18094/cart/addGoodsToCartList.shtml',{
                params:{
                    itemId:this.sku.id,
                    num:this.num
                },
                //允許跨域請求携帶cookie
                withCredentials:true
            }).then(function (response) {
                if(response.data.success){
                    window.location.href="http://localhost:18094/cart.html"
                }else {
                    alert(response.data.message)
                }
            })
        },

        //查询sku
        search:function(){
            //遍历skuList
            for(var i=0;i<skuList.length;i++){
                //比较
                if(JSON.stringify(skuList[i].spec)==JSON.stringify(this.specificationItems)){
                    //赋值
                    this.sku=skuList[i];
                    console.log(this.sku);
                    break;
                }
            }
        },

        //选择规格
        selectSpecifcation:function(name,value){
            this.$set(this.specificationItems,name,value);
            this.search()
        },

        //购买数量
        addNum:function(number){
            number=parseInt(number);
            this.num+=number;
            //判断值
            if(this.num<1) this.num=1;
            if(this.num>99) this.num=99;
        },

        //规格是否选中
        isSelected:function(name,value){
            if(this.specificationItems[name]==value){
                return true;
            }
            return false;
        }
    },

    created:function () {
    }

})
