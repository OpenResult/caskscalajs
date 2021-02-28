// vue.config.js

/**
 * @type {import('@vue/cli-service').ProjectOptions}
 */
module.exports = {
    publicPath: '/htm/',
    chainWebpack: config => {
        config.externals({
            'MainScalaJs': 'MainScalaJs'
        })
    }
}