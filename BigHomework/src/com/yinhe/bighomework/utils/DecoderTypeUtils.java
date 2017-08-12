package com.yinhe.bighomework.utils;

import com.yinhe.bighomework.obj.PMTInfo;
import com.yinhe.dtv.DvbPlayer;

public class DecoderTypeUtils {
	static public void setDecoderType(int StreamType, PMTInfo pmtInfo,
			int elementary_PID) {
		boolean video = false;
		int decoderType = ElementaryStream.INVALID_DECODER_TYPE;
		String strDecoderType = null;

		switch (StreamType) {
		case ElementaryStream.STREAM_TYPE_ISO_IEC_11172_2_VIDEO:
		case ElementaryStream.STREAM_TYPE_ISO_IEC_13818_2_VIDEO:
			video = true;
			decoderType = DvbPlayer.VIDEO_DECODER_TYPE_MPEG2;
			strDecoderType = "VIDEO_DECODER_TYPE_MPEG2";
			break;
		case ElementaryStream.STREAM_TYPE_ISO_IEC_14496_2_VISUAL:
			video = true;
			decoderType = DvbPlayer.VIDEO_DECODER_TYPE_MPEG4;
			strDecoderType = "VIDEO_DECODER_TYPE_MPEG4";
			break;
		case ElementaryStream.STREAM_TYPE_ITU_T_H264:
			video = true;
			decoderType = DvbPlayer.VIDEO_DECODER_TYPE_H264;
			strDecoderType = "VIDEO_DECODER_TYPE_H264";
			break;
		case ElementaryStream.STREAM_TYPE_AVS_VIDEO:
			video = true;
			decoderType = DvbPlayer.VIDEO_DECODER_TYPE_AVS;
			strDecoderType = "VIDEO_DECODER_TYPE_AVS";
			break;
		case ElementaryStream.STREAM_TYPE_HEVC_VIDEO:
			video = true;
			decoderType = DvbPlayer.VIDEO_DECODER_TYPE_HEVC;
			strDecoderType = "VIDEO_DECODER_TYPE_HEVC";
			break;
		case ElementaryStream.STREAM_TYPE_WM9_VIDEO:
			video = true;
			decoderType = DvbPlayer.VIDEO_DECODER_TYPE_VC1;
			strDecoderType = "VIDEO_DECODER_TYPE_VC1";
			break;

		case ElementaryStream.STREAM_TYPE_ISO_IEC_11172_3_AUDIO:
		case ElementaryStream.STREAM_TYPE_ISO_IEC_13818_3_AUDIO:
			video = false;
			decoderType = DvbPlayer.AUDIO_DECODER_TYPE_MP3;
			strDecoderType = "AUDIO_DECODER_TYPE_MP3";

			break;
		case ElementaryStream.STREAM_TYPE_ISO_IEC_13818_7_AUDIO:
		case ElementaryStream.STREAM_TYPE_ISO_IEC_14496_3_AUDIO:
		case ElementaryStream.STREAM_TYPE_ISO_IEC_14496_1_IN_PES:
			video = false;
			decoderType = DvbPlayer.AUDIO_DECODER_TYPE_AAC;
			strDecoderType = "AUDIO_DECODER_TYPE_AAC";

			break;
		case ElementaryStream.STREAM_TYPE_A52_AC3_AUDIO:
		case ElementaryStream.STREAM_TYPE_EAC3_AUDIO:
		case ElementaryStream.STREAM_TYPE_A52B_AC3_AUDIO:
			video = false;
			decoderType = DvbPlayer.AUDIO_DECODER_TYPE_DOLBY_PLUS;
			strDecoderType = "AUDIO_DECODER_TYPE_DOLBY_PLUS";

			break;
		case ElementaryStream.STREAM_TYPE_ATSC_PROGRAM_ID:
		case ElementaryStream.STREAM_TYPE_DTS_HD_AUDIO:
		case ElementaryStream.STREAM_TYPE_DTS_AUDIO:
			video = false;
			decoderType = DvbPlayer.AUDIO_DECODER_TYPE_DTSHD;
			strDecoderType = "AUDIO_DECODER_TYPE_DTSHD";

			break;
		case ElementaryStream.STREAM_TYPE_DRA_AUDIO:
			video = false;
			decoderType = DvbPlayer.AUDIO_DECODER_TYPE_DRA;
			strDecoderType = "AUDIO_DECODER_TYPE_DRA";

			break;
		case ElementaryStream.STREAM_TYPE_WM9_AUDIO:
			video = false;
			decoderType = DvbPlayer.AUDIO_DECODER_TYPE_WMA9STD;
			strDecoderType = "AUDIO_DECODER_TYPE_WMA9STD";

			break;
		case ElementaryStream.STREAM_TYPE_ISO_IEC_13818_1_PES:

			decoderType = DvbPlayer.AUDIO_DECODER_TYPE_DOLBY_PLUS;
			strDecoderType = "AUDIO_DECODER_TYPE_DOLBY_PLUS";

			// for (BaseDescriptor bd : loop.ES_info) {
			// switch (bd.getTag()) {
			// case SectionDefinition.REGISTRATION_DESCRIPTOR_TAG:
			// RegistrationDescriptor rd = (RegistrationDescriptor) bd;
			// switch (rd.format_identifier) {
			// case FORMAT_IDENTIFIER_AC3:
			// video = false;
			// decoderType = DvbPlayer.AUDIO_DECODER_TYPE_DOLBY_PLUS;
			// break;
			// case FORMAT_IDENTIFIER_DTS1:
			// case FORMAT_IDENTIFIER_DTS2:
			// case FORMAT_IDENTIFIER_DTS3:
			// video = false;
			// decoderType = DvbPlayer.AUDIO_DECODER_TYPE_DTSHD;
			// break;
			// default:
			// break;
			// }
			// break;
			// case SectionDefinition.AC3_DESCRIPTOR_TAG:
			// case SectionDefinition.EAC3_DESCRIPTOR_TAG:
			// video = false;
			// decoderType = DvbPlayer.AUDIO_DECODER_TYPE_DOLBY_PLUS;
			// break;
			// case SectionDefinition.DTS_AUDIO_STREAM_DESCRIPTOR_TAG:
			// video = false;
			// decoderType = DvbPlayer.AUDIO_DECODER_TYPE_DTSHD;
			// break;
			// case SectionDefinition.AAC_DESCRIPTOR_TAG:
			// video = false;
			// decoderType = DvbPlayer.AUDIO_DECODER_TYPE_AAC;
			// break;
			// default:
			// break;
			// }
			// if (decoderType != ElementaryStream.INVALID_DECODER_TYPE) break;
			// }
			//
			// break;
		default:
			return;
		}

		if (decoderType == ElementaryStream.INVALID_DECODER_TYPE)
			return;

		if (video) {
			pmtInfo.setVideoType(decoderType);
			pmtInfo.setVideoPid(elementary_PID);
			pmtInfo.setStrVideoType(strDecoderType);

		} else {
			pmtInfo.setAudioType(decoderType);
			pmtInfo.setAudioPid(elementary_PID);
			pmtInfo.setStrAudeoType(strDecoderType);
		}
	}
}
