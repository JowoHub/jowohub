#include "UrlManager.h"
#include <cstring>

/**
 * 链接地址管理类
 *
 *
 */
typedef struct {
	const char *key;
	const char *value;
} URL_MAP;

static const URL_MAP urlMap[] = {
		{"PRODUCT_HOST", "https://your-api.yourhost.com/"},//TODO 修改为自己的域名
        {"TEST_HOST", "https://test.testhost.com/"}
};

char *getUrlByKey(char const *inKey) {
	int i = 0;
	// 数组长度
	int len = sizeof(urlMap) / sizeof(URL_MAP);
	while (i < len) {
		if (strcmp(urlMap[i].key, inKey) == 0) {
			return const_cast<char *>(urlMap[i].value);
		}
		i++;
	}
	return nullptr;
}
