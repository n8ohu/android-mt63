#include <jni.h>
#include "dsp.h"
#include "mt63.h"

using namespace std;

MT63tx tx;

extern "C" {
JNIEXPORT void JNICALL Java_net_thinkindifferent_mt63_Modulator_initTx(JNIEnv *env, jobject obj) {
	tx.Preset(2000, 1);
}

JNIEXPORT jint JNICALL Java_net_thinkindifferent_mt63_Modulator_processText(JNIEnv* env, jobject obj, jstring text, jshortArray buffer) {
	const char *textBytes = env->GetStringUTFChars(text, NULL);
	s16_buff OutBuff;
	int pos = 0;
	for(int i = 0; i < env->GetStringUTFLength(text); ++i) {
		tx.SendChar(textBytes[i]);
		ConvFloatToS16(&tx.Comb.Output, &OutBuff);
		env->SetShortArrayRegion(buffer, pos, OutBuff.Len, OutBuff.Data);
		pos += OutBuff.Len;
	}
	env->ReleaseStringUTFChars(text, textBytes);
	for(int i = 0; i < tx.DataInterleave; ++i) {
		tx.SendChar(0);
		ConvFloatToS16(&tx.Comb.Output, &OutBuff);
		env->SetShortArrayRegion(buffer, pos, OutBuff.Len, OutBuff.Data);
		pos += OutBuff.Len;
	}
	tx.SendJam();
	ConvFloatToS16(&tx.Comb.Output, &OutBuff);
	env->SetShortArrayRegion(buffer, pos, OutBuff.Len, OutBuff.Data);
	pos += OutBuff.Len;
	tx.SendSilence();
	ConvFloatToS16(&tx.Comb.Output, &OutBuff);
	env->SetShortArrayRegion(buffer, pos, OutBuff.Len, OutBuff.Data);
	pos += OutBuff.Len;
	return pos + 1;
}
}
