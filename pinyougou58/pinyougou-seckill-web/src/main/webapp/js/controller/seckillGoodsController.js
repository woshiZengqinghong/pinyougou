var app = new Vue({
    el: "#app",
    data: {
        pages: 15,
        pageNo: 1,
        list: [],
        entity: {},
        ids: [],
        searchEntity: {}
    },
    methods: {
        //查询所有的商品的列表
        findAll: function () {
            axios.get('/seckillGoods/findByStatus.shtml').then(
                function (response) {//List<>
                    app.list = response.data;
                }
            )
        }
    },
    created: function () {
        //加载调用
        this.findAll();
    }
});