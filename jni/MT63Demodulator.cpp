#include <jni.h>
#include "dsp.h"
#include "mt63.h"

using namespace std;

MT63rx rx;

extern "C" {
JNIEXPORT void JNICALL Java_net_thinkindifferent_mt63_Demodulator_initRx(JNIEnv *env, jobject obj) {
	rx.Preset(2000, 1, 32);
}

JNIEXPORT jstring JNICALL Java_net_thinkindifferent_mt63_Demodulator_processAudio(JNIEnv* env, jobject obj, jshortArray audio) {
	jsize dataLen = env->GetArrayLength(audio);
	jshort *data = env->GetShortArrayElements(audio, 0);
	float_buff floatData;
	ConvS16toFloat(data, &floatData, dataLen);
	rx.Process(&floatData);
	char output[rx.Output.Len + 1];
	for(int i = 0; i < rx.Output.Len; ++i) {
		output[i] = rx.Output.Data[i];
	}
	output[rx.Output.Len] = '\0';
	env->ReleaseShortArrayElements(audio, data, 0);
	return env->NewStringUTF(output);
}
}
