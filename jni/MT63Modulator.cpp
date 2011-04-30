#include <jni.h>
#include "dsp.h"
#include "mt63.h"

using namespace std;

extern "C" {
JNIEXPORT jshortArray JNICALL Java_net_thinkindifferent_mt63_Modulator_processText(JNIEnv* env, jobject obj, jstring text) {
	MT63tx tx;
	tx.Preset(2000, 1);
	jsize textLen = env->GetStringUTFLength(text);
	int totalLen = 0;
	jobject *outputChars[textLen];
	jsize charLengths[textLen];
	const char *textBytes = env->GetStringUTFChars(text, NULL);
	for(int i = 0; i < textLen; ++i) {
		tx.SendChar(textBytes[i]);
		charLengths[i] = tx.Comb.Output.Len;
		totalLen += charLengths[i];
		outputChars[i] = new jobject[charLengths[i]];
		for(int j = 0; j < charLengths[i]; ++j)
			outputChars[i][j] = env->NewObject(env->FindClass("short"), env->GetMethodID(env->FindClass("short"), "<init>", "(S)V"), tx.Comb.Output.Data[j]);
	}
	env->ReleaseStringUTFChars(text, textBytes);
	jobject *outputNuls[tx.DataInterleave];
	jsize nulLengths[tx.DataInterleave];
	for(int i = 0; i < tx.DataInterleave; ++i) {
		tx.SendChar(0);
		nulLengths[i] = tx.Comb.Output.Len;
		totalLen += nulLengths[i];
		outputNuls[i] = new jobject[nulLengths[i]];
		for(int j = 0; j < nulLengths[i]; ++j)
			outputNuls[i][j] = env->NewObject(env->FindClass("short"), env->GetMethodID(env->FindClass("short"), "<init>", "(S)V"), tx.Comb.Output.Data[j]);
	}
	tx.SendJam();
	jsize jamLen = tx.Comb.Output.Len;
	jobject outputJam[jamLen];
	totalLen += jamLen;
	for(int i = 0; i < jamLen; ++i)
		outputJam[i] = env->NewObject(env->FindClass("short"), env->GetMethodID(env->FindClass("short"), "<init>", "(S)V"), tx.Comb.Output.Data[i]);
	tx.SendSilence();
	jsize silenceLen = tx.Comb.Output.Len;
	totalLen += silenceLen;
	jobject outputSilence[silenceLen];
	for(int i = 0; i < silenceLen; ++i)
		outputSilence[i] = env->NewObject(env->FindClass("short"), env->GetMethodID(env->FindClass("short"), "<init>", "(S)V"), tx.Comb.Output.Data[i]);
	jobjectArray output = env->NewObjectArray(totalLen, env->FindClass("short"), 0);
	jsize ptr = 0;
	for(int i = 0; i < textLen; ++i)
		for(int j = 0; j < charLengths[i]; ++j)
			env->SetObjectArrayElement(output, ptr++, outputChars[i][j]);
	for(int i = 0; i < tx.DataInterleave; ++i)
		for(int j = 0; j < nulLengths[i]; ++j)
			env->SetObjectArrayElement(output, ptr++, outputNuls[i][j]);
	for(int i = 0; i < jamLen; ++i)
		env->SetObjectArrayElement(output, ptr++, outputJam[i]);
	for(int i = 0; i < silenceLen; ++i)
		env->SetObjectArrayElement(output, ptr++, outputSilence[i]);
	for(int i = 0; i < tx.DataInterleave; ++i)
		delete outputNuls[i];
	for(int i = 0; i < textLen; ++i)
		delete outputChars[i];
}
}
