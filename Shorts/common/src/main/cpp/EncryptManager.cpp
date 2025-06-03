#include "EncryptManager.h"
#include <cstring>

/**
 * 加密秘钥管理类
 *
 */

typedef struct {
	const char *key;
	const char *value;
} ENCRYPT_MAP;

// 初始化加密的公钥及秘钥
static const ENCRYPT_MAP encrypt_Map[] = {
        {"APP_ID", "APP000001"},
        {"AES_KEY", "INgXrnOmhQpR2E3g"},
        {"AES_IV", "AzA4lNVtijdjFqX8"},
        //[改] 以下三个值对应的app在jowo那边的值 只需要修改这三个值即可
        {"JOWO_APP_ID", "JW000001"},
        {"JOWO_AES_KEY", "XXEvnDCQo88fmefW"},
        {"JOWO_AES_IV", "C2wWQwzjPqB8U6xU"}
};

char *getEncryptByKey(const char *inKey) {
	int i = 0;
	// 数组长度
	int len = sizeof(encrypt_Map) / sizeof(ENCRYPT_MAP);
	while (i < len) {
		if (strcmp(encrypt_Map[i].key, inKey) == 0) {
			return const_cast<char *>(encrypt_Map[i].value);
		}
		i++;
	}
	return nullptr;
}
