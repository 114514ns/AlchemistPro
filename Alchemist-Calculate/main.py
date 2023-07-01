import timeit

def test_cache_speed():
    """测试CPU缓存速度"""
    # 使用1024 * 1024个int值进行测试，每个int占用4字节，共4MB大小
    array_size = 1024 * 1024
    data = list(range(array_size))

    # 测试访问数组元素的速度
    def access_array():
        for i in range(array_size):
            x = data[i]

    # 测试访问数组元素的速度（每次跳过一个元素）
    def access_array_stride():
        stride = 16
        for i in range(0, array_size, stride):
            x = data[i]

    # 测试执行时间
    print("访问连续数组元素时间：", timeit.timeit(access_array, number=10000))
    print("访问跳过元素的数组元素时间：", timeit.timeit(access_array_stride, number=10000))

if __name__ == "__main__":
    test_cache_speed()