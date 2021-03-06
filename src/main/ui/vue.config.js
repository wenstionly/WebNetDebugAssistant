module.exports = {
  outputDir: '../resources/ui',
  publicPath: './',
  productionSourceMap: false,

  css: {
    sourceMap: false,
  },
  lintOnSave: false,

  devServer: {
    port: 8090,
    proxy: {
      '/api': {
        target: 'http://localhost:9090/',
        pathRewrite: {
          '^/api': '/api'
        },
        headers: {
          'Cache-Control': 'no-store'
        }
      },
      '/ws': {
        target: 'ws://localhost:9090/',
        ws: true,
        changeOrigin: true,
        pathRewrite: {
          '^/ws': '/ws'
        },
      }
    }
  }
}