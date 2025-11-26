<template>
  <div class="dashboard">
    <!-- 欢迎卡片 -->
    <el-card class="welcome-card">
      <template #header>
        <div class="welcome-header">
          <el-avatar :size="64" :src="userInfo.avatar">
            {{ userInfo?.name?.charAt(0) }}
          </el-avatar>
          <div class="welcome-info">
            <h2>欢迎回来, {{ userInfo?.name || userInfo?.username }}</h2>
            <p>{{ currentTime }}</p>
          </div>
        </div>
      </template>
      <div class="role-info">
        <el-tag>{{ roleLabel }}</el-tag>
      </div>
    </el-card>

    <!-- 统计卡片区域 -->
    <div class="stats-section" v-loading="statsLoading">
      <!-- 主要统计指标 -->
      <div class="stats-grid">
        <el-card class="stat-card user-stats">
          <div class="stat-content">
            <div class="stat-icon">
              <i class="fas fa-users"></i>
            </div>
            <div class="stat-info">
              <h3>{{ statsData?.userStats?.totalUsers || 0 }}</h3>
              <p>总用户数</p>
              <div class="stat-detail">
                <span>活跃: {{ statsData?.userStats?.activeUsers || 0 }}</span>
                <span>今日新增: {{ statsData?.userStats?.todayNewUsers || 0 }}</span>
              </div>
            </div>
          </div>
        </el-card>

        <el-card class="stat-card snack-stats">
          <div class="stat-content">
            <div class="stat-icon">
              <i class="fas fa-cookie-bite"></i>
            </div>
            <div class="stat-info">
              <h3>{{ statsData?.snackStats?.totalSnacks || 0 }}</h3>
              <p>总商品数</p>
              <div class="stat-detail">
                <span>上架: {{ statsData?.snackStats?.onSaleSnacks || 0 }}</span>
                <span>库存不足: {{ statsData?.snackStats?.lowStockSnacks || 0 }}</span>
              </div>
            </div>
          </div>
        </el-card>

        <el-card class="stat-card order-stats">
          <div class="stat-content">
            <div class="stat-icon">
              <i class="fas fa-shopping-cart"></i>
            </div>
            <div class="stat-info">
              <h3>{{ statsData?.orderStats?.totalOrders || 0 }}</h3>
              <p>总订单数</p>
              <div class="stat-detail">
                <span>今日: {{ statsData?.orderStats?.todayOrders || 0 }}</span>
                <span>待处理: {{ statsData?.orderStats?.pendingOrders || 0 }}</span>
              </div>
            </div>
          </div>
        </el-card>

        <el-card class="stat-card revenue-stats">
          <div class="stat-content">
            <div class="stat-icon">
              <i class="fas fa-dollar-sign"></i>
            </div>
            <div class="stat-info">
              <h3>¥{{ statsData?.orderStats?.totalAmount || 0 }}</h3>
              <p>总销售额</p>
              <div class="stat-detail">
                <span>今日: ¥{{ statsData?.orderStats?.todayAmount || 0 }}</span>
                <span>本月: ¥{{ statsData?.orderStats?.monthAmount || 0 }}</span>
              </div>
            </div>
          </div>
        </el-card>
      </div>

        <!-- ECharts 图表区域 -->
        <div class="charts-section">
          <div class="charts-row">
            <!-- 订单趋势图 -->
            <el-card class="chart-card">
              <template #header>
                <div class="card-header">
                  <span><i class="fas fa-chart-line"></i> 订单趋势</span>
                </div>
              </template>
              <div ref="orderTrendChart" class="chart-container"></div>
            </el-card>

            <!-- 销售额饼图 -->
            <el-card class="chart-card">
              <template #header>
                <div class="card-header">
                  <span><i class="fas fa-chart-pie"></i> 销售额分布</span>
                </div>
              </template>
              <div ref="revenuePieChart" class="chart-container"></div>
            </el-card>
          </div>

          <div class="charts-row">
            <!-- 商品分类统计柱状图 -->
            <el-card class="chart-card">
              <template #header>
                <div class="card-header">
                  <span><i class="fas fa-chart-bar"></i> 商品分类统计</span>
                </div>
              </template>
              <div ref="categoryBarChart" class="chart-container"></div>
            </el-card>

            <!-- 用户增长趋势图 -->
            <el-card class="chart-card">
              <template #header>
                <div class="card-header">
                  <span><i class="fas fa-users"></i> 用户增长趋势</span>
                </div>
              </template>
              <div ref="userGrowthChart" class="chart-container"></div>
            </el-card>
          </div>
        </div>

        <!-- 详细统计区域 -->
        <div class="detail-stats">
          <div class="stats-row">
            <!-- 热门商品 -->
            <el-card class="detail-card">
              <template #header>
                <div class="card-header">
                  <span><i class="fas fa-fire"></i> 热门商品</span>
                </div>
              </template>
              <div class="popular-snacks">
                <div 
                  v-for="snack in statsData?.popularSnacks" 
                  :key="snack.id"
                  class="popular-item"
                >
                  <div class="item-image">
                    <img :src="snack.coverImage" :alt="snack.name" />
                  </div>
                  <div class="item-info">
                    <h4>{{ snack.name }}</h4>
                    <p>销量: {{ snack.salesCount }}</p>
                    <span class="price">¥{{ snack.price }}</span>
                  </div>
                </div>
              </div>
            </el-card>

            <!-- 最近订单 -->
            <el-card class="detail-card">
              <template #header>
                <div class="card-header">
                  <span><i class="fas fa-clock"></i> 最近订单</span>
                </div>
              </template>
              <div class="recent-orders">
                <div 
                  v-for="order in statsData?.recentOrders" 
                  :key="order.id"
                  class="order-item"
                >
                  <div class="order-info">
                    <h4>{{ order.orderNo }}</h4>
                    <p>用户: {{ order.username }}</p>
                    <span class="amount">¥{{ order.totalAmount }}</span>
                  </div>
                  <div class="order-status">
                    <el-tag :type="getOrderStatusType(order.status)">
                      {{ getOrderStatusText(order.status) }}
                    </el-tag>
                    <span class="time">{{ order.createTime }}</span>
                  </div>
                </div>
              </div>
            </el-card>
          </div>

        <!-- 其他统计指标 -->
        <div class="stats-row">
          <el-card class="mini-stat-card">
            <div class="mini-stat-content">
              <i class="fas fa-layer-group"></i>
              <div>
                <h4>{{ statsData?.categoryStats?.totalCategories || 0 }}</h4>
                <p>商品分类</p>
              </div>
            </div>
          </el-card>

          <el-card class="mini-stat-card">
            <div class="mini-stat-content">
              <i class="fas fa-heart"></i>
              <div>
                <h4>{{ statsData?.favoriteStats?.totalFavorites || 0 }}</h4>
                <p>总收藏数</p>
              </div>
            </div>
          </el-card>

          <el-card class="mini-stat-card">
            <div class="mini-stat-content">
              <i class="fas fa-shopping-basket"></i>
              <div>
                <h4>{{ statsData?.cartStats?.activeCartCount || 0 }}</h4>
                <p>活跃购物车</p>
              </div>
            </div>
          </el-card>

          <el-card class="mini-stat-card">
            <div class="mini-stat-content">
              <i class="fas fa-chart-line"></i>
              <div>
                <h4>{{ statsData?.orderStats?.completedOrders || 0 }}</h4>
                <p>已完成订单</p>
              </div>
            </div>
          </el-card>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { computed, ref, onMounted, onUnmounted, nextTick } from 'vue'
import { useRouter } from 'vue-router'
import { useUserStore } from '@/store/user'
import { getDashboardStats } from '@/api/dashboard'
import { ElMessage } from 'element-plus'
import * as echarts from 'echarts'

const router = useRouter()
const userStore = useUserStore()
const userInfo = computed(() => userStore.userInfo)

// 角色标签
const roleLabel = computed(() => {
  const roleMap = {
    'ADMIN': '管理员',
    'USER': '普通用户'
  }
  return roleMap[userInfo.value?.userType] || '未知角色'
})

// 统计数据
const statsData = ref(null)
const statsLoading = ref(false)

// ECharts 图表实例引用
const orderTrendChart = ref(null)
const revenuePieChart = ref(null)
const categoryBarChart = ref(null)
const userGrowthChart = ref(null)

// ECharts 实例
let orderTrendInstance = null
let revenuePieInstance = null
let categoryBarInstance = null
let userGrowthInstance = null

// 获取统计数据
const fetchDashboardStats = () => {
  statsLoading.value = true
  getDashboardStats({
    onSuccess: (res) => {
      statsData.value = res
      console.log('仪表板统计数据:', res)
      statsLoading.value = false
      // 数据获取成功后初始化图表
      nextTick(() => {
        initCharts()
      })
    },
    onError: (error) => {
      console.error('获取统计数据失败:', error)
      ElMessage.error('获取统计数据失败')
      statsLoading.value = false
    }
  })
}

// 初始化所有图表
const initCharts = () => {
  initOrderTrendChart()
  initRevenuePieChart()
  initCategoryBarChart()
  initUserGrowthChart()
}

// 初始化订单趋势图
const initOrderTrendChart = () => {
  if (!orderTrendChart.value || !statsData.value?.orderTrendData || statsData.value.orderTrendData.length === 0) return
  
  orderTrendInstance = echarts.init(orderTrendChart.value)
  
  // 使用后端真实数据
  const dates = statsData.value.orderTrendData.map(item => {
    // 将 YYYY-MM-DD 格式转换为 MM-DD 显示
    const dateParts = item.date.split('-')
    return `${dateParts[1]}-${dateParts[2]}`
  })
  const orderCounts = statsData.value.orderTrendData.map(item => item.orderCount || 0)
  const revenues = statsData.value.orderTrendData.map(item => item.revenue || 0)
  
  const option = {
    title: {
      text: '最近7天订单趋势',
      left: 'center',
      textStyle: {
        fontSize: 14,
        color: '#303133'
      }
    },
    tooltip: {
      trigger: 'axis'
    },
    legend: {
      data: ['订单数量', '销售额'],
      bottom: 10
    },
    xAxis: {
      type: 'category',
      data: dates,
      axisLabel: {
        fontSize: 12
      }
    },
    yAxis: [
      {
        type: 'value',
        name: '订单数量',
        position: 'left',
        axisLabel: {
          formatter: '{value}单'
        }
      },
      {
        type: 'value',
        name: '销售额',
        position: 'right',
        axisLabel: {
          formatter: '{value}元'
        }
      }
    ],
    series: [
      {
        name: '订单数量',
        type: 'line',
        data: orderCounts,
        smooth: true,
        itemStyle: {
          color: '#409EFF'
        }
      },
      {
        name: '销售额',
        type: 'bar',
        yAxisIndex: 1,
        data: revenues,
        itemStyle: {
          color: '#67C23A'
        }
      }
    ]
  }
  
  orderTrendInstance.setOption(option)
}

// 初始化销售额饼图
const initRevenuePieChart = () => {
  if (!revenuePieChart.value || !statsData.value) return
  
  revenuePieInstance = echarts.init(revenuePieChart.value)
  
  const option = {
    title: {
      text: '销售额分布',
      left: 'center',
      textStyle: {
        fontSize: 14,
        color: '#303133'
      }
    },
    tooltip: {
      trigger: 'item',
      formatter: '{a} <br/>{b}: ¥{c} ({d}%)'
    },
    legend: {
      orient: 'vertical',
      left: 10,
      data: ['今日', '本周', '本月', '其他']
    },
    series: [
      {
        name: '销售额',
        type: 'pie',
        radius: ['40%', '70%'],
        center: ['60%', '50%'],
        data: [
          { value: statsData.value.orderStats?.todayAmount || 0, name: '今日' },
          { value: (statsData.value.orderStats?.monthAmount || 0) * 0.3, name: '本周' },
          { value: statsData.value.orderStats?.monthAmount || 0, name: '本月' },
          { value: (statsData.value.orderStats?.totalAmount || 0) * 0.2, name: '其他' }
        ],
        emphasis: {
          itemStyle: {
            shadowBlur: 10,
            shadowOffsetX: 0,
            shadowColor: 'rgba(0, 0, 0, 0.5)'
          }
        },
        itemStyle: {
          borderRadius: 8,
          borderColor: '#fff',
          borderWidth: 2
        }
      }
    ]
  }
  
  revenuePieInstance.setOption(option)
}

// 初始化商品分类统计柱状图
const initCategoryBarChart = () => {
  if (!categoryBarChart.value || !statsData.value?.categorySalesData || statsData.value.categorySalesData.length === 0) return
  
  categoryBarInstance = echarts.init(categoryBarChart.value)
  
  // 使用后端真实数据
  const categories = statsData.value.categorySalesData.map(item => item.categoryName || '未知分类')
  const sales = statsData.value.categorySalesData.map(item => item.salesCount || 0)
  
  const option = {
    title: {
      text: '商品分类销量统计',
      left: 'center',
      textStyle: {
        fontSize: 14,
        color: '#303133'
      }
    },
    tooltip: {
      trigger: 'axis',
      axisPointer: {
        type: 'shadow'
      }
    },
    xAxis: {
      type: 'category',
      data: categories,
      axisLabel: {
        fontSize: 12,
        interval: 0,
        rotate: 45
      }
    },
    yAxis: {
      type: 'value',
      name: '销量',
      axisLabel: {
        formatter: '{value}件'
      }
    },
    series: [
      {
        name: '销量',
        type: 'bar',
        data: sales,
        itemStyle: {
          color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [
            { offset: 0, color: '#ffd700' },
            { offset: 1, color: '#ff8c00' }
          ])
        },
        barWidth: '60%'
      }
    ]
  }
  
  categoryBarInstance.setOption(option)
}

// 初始化用户增长趋势图
const initUserGrowthChart = () => {
  if (!userGrowthChart.value || !statsData.value?.userGrowthData || statsData.value.userGrowthData.length === 0) return
  
  userGrowthInstance = echarts.init(userGrowthChart.value)
  
  // 使用后端真实数据
  const months = statsData.value.userGrowthData.map(item => {
    // 将 YYYY-MM 格式转换为 MM月 显示
    const monthPart = item.month.split('-')[1]
    return `${monthPart}月`
  })
  const userGrowth = statsData.value.userGrowthData.map(item => item.newUsers || 0)
  
  const option = {
    title: {
      text: '用户增长趋势（最近12个月）',
      left: 'center',
      textStyle: {
        fontSize: 14,
        color: '#303133'
      }
    },
    tooltip: {
      trigger: 'axis'
    },
    xAxis: {
      type: 'category',
      data: months,
      axisLabel: {
        fontSize: 12
      }
    },
    yAxis: {
      type: 'value',
      name: '新增用户',
      axisLabel: {
        formatter: '{value}人'
      }
    },
    series: [
      {
        name: '新增用户',
        type: 'line',
        data: userGrowth,
        smooth: true,
        areaStyle: {
          color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [
            { offset: 0, color: 'rgba(64, 158, 255, 0.3)' },
            { offset: 1, color: 'rgba(64, 158, 255, 0.1)' }
          ])
        },
        itemStyle: {
          color: '#409EFF'
        }
      }
    ]
  }
  
  userGrowthInstance.setOption(option)
}

// 销毁图表实例
const destroyCharts = () => {
  if (orderTrendInstance) {
    orderTrendInstance.dispose()
    orderTrendInstance = null
  }
  if (revenuePieInstance) {
    revenuePieInstance.dispose()
    revenuePieInstance = null
  }
  if (categoryBarInstance) {
    categoryBarInstance.dispose()
    categoryBarInstance = null
  }
  if (userGrowthInstance) {
    userGrowthInstance.dispose()
    userGrowthInstance = null
  }
}

// 响应式调整图表大小
const resizeCharts = () => {
  if (orderTrendInstance) orderTrendInstance.resize()
  if (revenuePieInstance) revenuePieInstance.resize()
  if (categoryBarInstance) categoryBarInstance.resize()
  if (userGrowthInstance) userGrowthInstance.resize()
}

// 获取订单状态类型
const getOrderStatusType = (status) => {
  const statusMap = {
    'PENDING': 'warning',
    'PROCESSING': 'primary',
    'SHIPPED': 'info',
    'COMPLETED': 'success',
    'CANCELLED': 'danger'
  }
  return statusMap[status] || 'info'
}

// 获取订单状态文本
const getOrderStatusText = (status) => {
  const statusMap = {
    'PENDING': '待处理',
    'PROCESSING': '处理中',
    'SHIPPED': '已发货',
    'COMPLETED': '已完成',
    'CANCELLED': '已取消'
  }
  return statusMap[status] || '未知'
}

// 当前时间
const currentTime = ref('')
let timeInterval = null // 保存定时器引用

const updateTime = () => {
  const now = new Date()
  const options = { 
    year: 'numeric', 
    month: 'long', 
    day: 'numeric', 
    weekday: 'long',
    hour: '2-digit',
    minute: '2-digit'
  }
  currentTime.value = now.toLocaleDateString('zh-CN', options)
}

onMounted(() => {
  updateTime()
  // 每分钟更新一次时间
  timeInterval = setInterval(updateTime, 60000)
  
  // 获取统计数据
  fetchDashboardStats()
  
  // 监听窗口大小变化
  window.addEventListener('resize', resizeCharts)
})

onUnmounted(() => {
  // 清除定时器
  if (timeInterval) {
    clearInterval(timeInterval)
    timeInterval = null
  }
  
  // 销毁图表实例
  destroyCharts()
  
  // 移除窗口大小变化监听
  window.removeEventListener('resize', resizeCharts)
})
</script>

<style lang="scss" scoped>
.dashboard {
  padding: 20px;
  background-color: #f5f7fa;
  min-height: calc(100vh - 60px);
}

.welcome-card {
  margin-bottom: 20px;
  border-radius: 12px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.1);

  .welcome-header {
    display: flex;
    align-items: center;
    gap: 20px;

    .welcome-info {
      h2 {
        margin: 0 0 8px 0;
        color: #303133;
        font-weight: 600;
      }
      
      p {
        margin: 0;
        color: #909399;
        font-size: 14px;
      }
    }
  }

  .role-info {
    margin-top: 15px;
  }
}

.stats-section {
  .stats-grid {
    display: grid;
    grid-template-columns: repeat(auto-fit, minmax(280px, 1fr));
    gap: 20px;
    margin-bottom: 30px;

    .stat-card {
      border-radius: 12px;
      box-shadow: 0 2px 12px rgba(0, 0, 0, 0.1);
      transition: all 0.3s ease;

      &:hover {
        transform: translateY(-2px);
        box-shadow: 0 4px 20px rgba(0, 0, 0, 0.15);
      }

      &.user-stats .stat-icon {
        background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
      }

      &.snack-stats .stat-icon {
        background: linear-gradient(135deg, #f093fb 0%, #f5576c 100%);
      }

      &.order-stats .stat-icon {
        background: linear-gradient(135deg, #4facfe 0%, #00f2fe 100%);
      }

      &.revenue-stats .stat-icon {
        background: linear-gradient(135deg, #43e97b 0%, #38f9d7 100%);
      }

      .stat-content {
        display: flex;
        align-items: center;
        padding: 20px;

        .stat-icon {
          width: 60px;
          height: 60px;
          border-radius: 12px;
          display: flex;
          align-items: center;
          justify-content: center;
          margin-right: 20px;

          i {
            font-size: 24px;
            color: white;
          }
        }

        .stat-info {
          flex: 1;

          h3 {
            font-size: 28px;
            font-weight: 700;
            margin: 0 0 5px 0;
            color: #303133;
          }

          p {
            margin: 0 0 10px 0;
            color: #606266;
            font-size: 14px;
            font-weight: 500;
          }

          .stat-detail {
            display: flex;
            gap: 15px;
            
            span {
              font-size: 12px;
              color: #909399;
              background: #f5f7fa;
              padding: 4px 8px;
              border-radius: 6px;
            }
          }
        }
      }
    }
  }

  .charts-section {
    margin-bottom: 30px;

    .charts-row {
      display: grid;
      grid-template-columns: 1fr 1fr;
      gap: 20px;
      margin-bottom: 20px;

      .chart-card {
        border-radius: 12px;
        box-shadow: 0 2px 12px rgba(0, 0, 0, 0.1);
        
        .card-header {
          font-weight: 600;
          color: #303133;
          
          i {
            margin-right: 8px;
            color: #409eff;
          }
        }

        .chart-container {
          width: 100%;
          height: 350px;
          padding: 10px;
        }
      }
    }
  }

  .detail-stats {
    .stats-row {
      display: grid;
      grid-template-columns: 1fr 1fr;
      gap: 20px;
      margin-bottom: 20px;

      &:last-child {
        grid-template-columns: repeat(4, 1fr);
      }

      .detail-card {
        border-radius: 12px;
        box-shadow: 0 2px 12px rgba(0, 0, 0, 0.1);

        .card-header {
          font-weight: 600;
          color: #303133;
          
          i {
            margin-right: 8px;
            color: #409eff;
          }
        }

        .popular-snacks {
          .popular-item {
            display: flex;
            align-items: center;
            padding: 12px 0;
            border-bottom: 1px solid #f0f0f0;

            &:last-child {
              border-bottom: none;
            }

            .item-image {
              width: 50px;
              height: 50px;
              border-radius: 8px;
              overflow: hidden;
              margin-right: 15px;

              img {
                width: 100%;
                height: 100%;
                object-fit: cover;
              }
            }

            .item-info {
              flex: 1;

              h4 {
                margin: 0 0 4px 0;
                font-size: 14px;
                color: #303133;
                font-weight: 600;
              }

              p {
                margin: 0 0 4px 0;
                font-size: 12px;
                color: #909399;
              }

              .price {
                font-size: 14px;
                color: #f56c6c;
                font-weight: 600;
              }
            }
          }
        }

        .recent-orders {
          .order-item {
            display: flex;
            justify-content: space-between;
            align-items: center;
            padding: 12px 0;
            border-bottom: 1px solid #f0f0f0;

            &:last-child {
              border-bottom: none;
            }

            .order-info {
              h4 {
                margin: 0 0 4px 0;
                font-size: 14px;
                color: #303133;
                font-weight: 600;
              }

              p {
                margin: 0 0 4px 0;
                font-size: 12px;
                color: #909399;
              }

              .amount {
                font-size: 14px;
                color: #67c23a;
                font-weight: 600;
              }
            }

            .order-status {
              text-align: right;

              .time {
                display: block;
                font-size: 11px;
                color: #c0c4cc;
                margin-top: 4px;
              }
            }
          }
        }
      }

      .mini-stat-card {
        border-radius: 12px;
        box-shadow: 0 2px 12px rgba(0, 0, 0, 0.1);
        transition: all 0.3s ease;

        &:hover {
          transform: translateY(-2px);
          box-shadow: 0 4px 20px rgba(0, 0, 0, 0.15);
        }

        .mini-stat-content {
          display: flex;
          align-items: center;
          padding: 20px;

          i {
            font-size: 20px;
            color: #409eff;
            margin-right: 15px;
            width: 30px;
            text-align: center;
          }

          h4 {
            margin: 0 0 2px 0;
            font-size: 20px;
            font-weight: 700;
            color: #303133;
          }

          p {
            margin: 0;
            font-size: 12px;
            color: #909399;
          }
        }
      }
    }
  }
}

@media (max-width: 768px) {
  .dashboard {
    padding: 10px;
  }

  .stats-section {
    .stats-grid {
      grid-template-columns: 1fr;
    }

    .charts-section {
      .charts-row {
        grid-template-columns: 1fr;
        
        .chart-card .chart-container {
          height: 300px;
        }
      }
    }

    .detail-stats .stats-row {
      grid-template-columns: 1fr;

      &:last-child {
        grid-template-columns: repeat(2, 1fr);
      }
    }
  }
}
</style> 