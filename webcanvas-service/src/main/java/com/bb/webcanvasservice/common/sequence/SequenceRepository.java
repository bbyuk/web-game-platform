package com.bb.webcanvasservice.common.sequence;

/**
 * 시퀀스 생성 및 채번 관련 레포지토리 인터페이스
 */
public interface SequenceRepository {
    
    /**
     * 대상 시퀀스명에 해당하는 시퀀스의 다음 값을 가져온다.
     * @param sequenceName 대상 시퀀스 명
     * @return 대상 시퀀스의 다음 값
     */
    long getNextValue(String sequenceName);

    /**
     * 대상 시퀀스명에 해당하는 시퀀스의 현재 값을 가져온다.
     * @param sequenceName 대상 시퀀스 명
     * @return 대상 시퀀스의 현재 값
     */
    long getCurrentValue(String sequenceName);

    /**
     * 시퀀스를 생성한다.
     * @param sequenceName 시퀀스 명
     */
    void createSequence(String sequenceName);

    /**
     * 대상 시퀀스명으로 생성되어 있는 시퀀스가 있는지 여부를 확인한다.
     * @param sequenceName 대상 시퀀스 명
     * @return 시퀀스 존재 여부
     */
    boolean isExistSequence(String sequenceName);
}
