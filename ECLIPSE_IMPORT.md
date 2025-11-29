# Eclipse에서 프로젝트 Import 하기

이 문서는 JavaAnalysis 프로젝트를 Eclipse IDE에서 import하는 방법을 설명합니다.

## 방법 1: Gradle 프로젝트로 직접 Import (권장)

Eclipse에 Buildship Gradle Integration 플러그인이 설치되어 있어야 합니다. (최신 Eclipse 버전에는 기본 포함)

### 단계:

1. **Eclipse 실행**

2. **File → Import 선택**

3. **Gradle → Existing Gradle Project 선택**
   - "Next" 클릭

4. **프로젝트 루트 디렉토리 선택**
   - "Project root directory"에서 "Browse" 클릭
   - `JavaAnalysis` 폴더 선택
   - "Next" 클릭

5. **Import Options**
   - "Override workspace settings" 체크 (선택사항)
   - Gradle distribution: "Gradle wrapper" 선택 (권장)
   - "Next" 클릭

6. **완료**
   - "Finish" 클릭
   - Eclipse가 자동으로 프로젝트를 빌드하고 의존성을 다운로드합니다

## 방법 2: Eclipse 프로젝트 파일 생성 후 Import

Gradle Wrapper가 없거나 수동으로 설정하고 싶은 경우:

### 단계:

1. **명령 프롬프트/터미널에서 프로젝트 디렉토리로 이동**
   ```bash
   cd D:\GitHub\JavaAnalysis\JavaAnalysis
   ```

2. **Eclipse 프로젝트 파일 생성**

   Windows:
   ```bash
   gradlew eclipse
   ```

   Linux/Mac:
   ```bash
   ./gradlew eclipse
   ```

   이 명령은 다음 파일들을 생성합니다:
   - `.project` - Eclipse 프로젝트 설정
   - `.classpath` - 클래스패스 설정
   - `.settings/` - 추가 Eclipse 설정

3. **Eclipse에서 Import**
   - File → Import 선택
   - General → Existing Projects into Workspace 선택
   - "Next" 클릭
   - "Select root directory"에서 프로젝트 디렉토리 선택
   - JavaAnalysis 프로젝트가 보이면 체크
   - "Finish" 클릭

## 확인사항

Import 후 다음을 확인하세요:

### 1. Java 버전 확인
- 프로젝트 우클릭 → Properties
- Java Build Path → Libraries
- JRE System Library가 Java 17 이상인지 확인

### 2. 의존성 확인
- Package Explorer에서 "Project and External Dependencies" 노드 확장
- 다음 라이브러리들이 있는지 확인:
  - javaparser-symbol-solver-core-3.25.8
  - plantuml-1.2023.13
  - slf4j-api-2.0.9
  - slf4j-simple-2.0.9

### 3. 소스 폴더 확인
- `src/main/java` - 메인 소스 코드
- `src/main/resources` - 리소스 파일
- `src/test/java` - 테스트 코드

## Eclipse에서 실행하기

### 1. 메인 클래스 실행

1. **UMLGenerator.java 파일 열기**
   - `src/main/java/com/javaanalysis/UMLGenerator.java`

2. **Run Configuration 생성**
   - 파일 우클릭 → Run As → Run Configurations...
   - Java Application → New Configuration
   - Name: "UML Generator - Sample"
   - Main class: `com.javaanalysis.UMLGenerator`
   - Arguments 탭:
     - Program arguments: `sample output/sample-diagram`
   - Apply → Run

### 2. Gradle Task 실행

1. **Gradle Tasks View 열기**
   - Window → Show View → Other...
   - Gradle → Gradle Tasks

2. **Task 실행**
   - JavaAnalysis → application → run (더블클릭)
   - 또는 JavaAnalysis → other → analyze (더블클릭)

## 문제 해결

### "Buildship Gradle Integration" 플러그인이 없는 경우

1. Help → Eclipse Marketplace
2. "Buildship" 검색
3. "Buildship Gradle Integration" 설치
4. Eclipse 재시작

### JRE 버전이 맞지 않는 경우

1. Window → Preferences
2. Java → Installed JREs
3. Add... → Standard VM
4. JDK 17 이상 경로 선택 및 추가
5. 프로젝트 우클릭 → Properties → Java Build Path → Libraries
6. JRE System Library 선택 → Edit
7. Alternate JRE에서 Java 17 선택

### 의존성이 다운로드되지 않는 경우

1. 프로젝트 우클릭
2. Gradle → Refresh Gradle Project

### 프로젝트가 빌드되지 않는 경우

1. Project → Clean...
2. JavaAnalysis 프로젝트 선택
3. OK 클릭

## Eclipse에서 디버깅

1. UMLGenerator.java에서 브레이크포인트 설정
2. 파일 우클릭 → Debug As → Java Application
3. Debug Perspective에서 변수 확인 및 스텝 실행

## 추가 정보

- Eclipse에서 코드를 수정한 후 자동으로 컴파일됩니다
- Ctrl+Shift+O (Windows) / Cmd+Shift+O (Mac): Import 자동 정리
- Ctrl+Shift+F (Windows) / Cmd+Shift+F (Mac): 코드 포맷팅

## 관련 링크

- [Eclipse IDE](https://www.eclipse.org/downloads/)
- [Buildship Gradle Plugin](https://projects.eclipse.org/projects/tools.buildship)
- [프로젝트 README](README.md)
