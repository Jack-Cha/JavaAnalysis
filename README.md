# Java Analysis - UML Diagram Generator

자바 소스 코드를 분석하여 UML 클래스 다이어그램을 자동으로 생성하는 도구입니다.

A tool that analyzes Java source code and automatically generates UML class diagrams.

## Features / 주요 기능

- **Class Diagram Generation** (클래스 다이어그램 생성)
  - 자바 소스 파일 자동 분석 (Automatic Java source file analysis)
  - 클래스, 인터페이스, Enum, 추상 클래스 지원 (Support for classes, interfaces, enums, and abstract classes)
  - 클래스 관계 자동 추출: 상속(Inheritance), 구현(Implementation), 의존(Dependencies)
- **Sequence Diagram Generation** (시퀀스 다이어그램 생성)
  - 메서드 호출 흐름 분석 (Method call flow analysis)
  - 특정 진입점(클래스/메서드) 기준 동작 시각화
- PlantUML 형식 지원 (PlantUML format support)
- PNG, SVG 이미지 자동 생성 (Automatic PNG and SVG image generation)

## Requirements / 필요 사항

- Java 17 이상 (Java 17 or higher)
- Gradle (for building)
- Eclipse IDE (optional, for development)

## Project Structure / 프로젝트 구조

```
JavaAnalysis/
├── build.gradle              # Gradle 빌드 설정
├── settings.gradle           # Gradle 설정
├── src/
│   └── main/
│       └── java/
│           └── com/
│               └── javaanalysis/
│                   ├── UMLGenerator.java          # 메인 애플리케이션
│                   ├── JavaSourceAnalyzer.java    # 자바 소스 분석기 (Class Diagram)
│                   ├── SequenceAnalyzer.java      # 시퀀스 분석기 (Sequence Diagram)
│                   ├── PlantUMLGenerator.java     # 클래스 다이어그램 생성기
│                   ├── SequencePlantUMLGenerator.java # 시퀀스 다이어그램 생성기
│                   ├── ClassInfo.java             # 클래스 정보 모델
│                   ├── FieldInfo.java             # 필드 정보 모델
│                   ├── MethodInfo.java            # 메서드 정보 모델
│                   └── ParameterInfo.java         # 파라미터 정보 모델
└── sample/                   # 테스트용 샘플 자바 파일
    ├── Animal.java
    ├── Dog.java
    ├── Cat.java
    ├── Mammal.java
    ├── Pet.java
    ├── Food.java
    └── FoodType.java
```

## Eclipse IDE에서 사용하기 / Using with Eclipse IDE

이 프로젝트는 Eclipse IDE에서 import하여 사용할 수 있습니다.

### 방법 1: Gradle 프로젝트로 직접 Import (권장)

1. Eclipse에서 **File → Import**
2. **Gradle → Existing Gradle Project** 선택
3. 프로젝트 루트 디렉토리 선택 (`JavaAnalysis` 폴더)
4. **Finish** 클릭

### 방법 2: Eclipse 프로젝트 파일 생성 후 Import

```bash
# Eclipse 프로젝트 파일 생성
gradlew eclipse

# Eclipse에서 File → Import → General → Existing Projects into Workspace
```

자세한 내용은 [ECLIPSE_IMPORT.md](ECLIPSE_IMPORT.md) 파일을 참고하세요.

## Build / 빌드

### Using Gradle Wrapper (권장)

Windows:
```bash
gradlew build
```

Linux/Mac:
```bash
./gradlew build
```

### Using System Gradle

```bash
gradle build
```

## Usage / 사용법

### 1. Class Diagram Generation (클래스 다이어그램 생성)

**Gradle 실행:**
```bash
# 기본 (sample 디렉토리 분석)
gradlew run

# 특정 디렉토리 분석
gradlew run --args="src/main/java output/my-class-diagram"
```

**JAR 실행:**
```bash
java -jar build/libs/JavaAnalysis-1.0.0.jar <source-directory> [output-base-path]
```

### 2. Sequence Diagram Generation (시퀀스 다이어그램 생성)

**Gradle 실행:**
```bash
# 기본 (sample/Cat.play 분석)
gradlew sequence

# 사용자 정의 분석
gradlew sequence -PsourceDir=src/main/java -PclassName=UMLGenerator -PmethodName=main -PoutputDir=output/main-seq
```

**JAR 실행:**
```bash
java -jar build/libs/JavaAnalysis-1.0.0.jar -sequence <source-dir> <class-name> <method-name> [output-path]
```

## Command Line Arguments / 명령줄 인자

### Class Diagram Mode
```
java -jar JavaAnalysis.jar <source-directory> [output-base-path]
```
- `source-directory`: 분석할 소스 폴더 (필수)
- `output-base-path`: 출력 경로 (선택)

### Sequence Diagram Mode
```
java -jar JavaAnalysis.jar -sequence <source-directory> <class-name> <method-name> [output-base-path]
```
- `-sequence`: 시퀀스 다이어그램 모드 플래그
- `source-directory`: 소스 폴더 (예: `sample`, `src/main/java`)
- `class-name`: 분석할 클래스 이름 (예: `Cat`)
- `method-name`: 분석할 메서드 이름 (예: `play`)
- `output-base-path`: 출력 경로 (선택)

## Output Files / 출력 파일

프로그램은 다음 파일들을 생성합니다:

1. `<output-base-path>.puml` - PlantUML 소스 파일
2. `<output-base-path>.png` - PNG 형식의 UML 다이어그램
3. `<output-base-path>.svg` - SVG 형식의 UML 다이어그램 (벡터 이미지, 고품질)

## Example / 예제

### Class Diagram Example
```bash
gradlew run --args="sample output/sample-diagram"
```

### Sequence Diagram Example
```bash
# sample/Cat.java의 play() 메서드 분석
java -jar JavaAnalysis.jar -sequence sample Cat play output/cat-play-seq
```
출력:
```
Generating Sequence Diagram...
Source: sample
Entry Point: Cat.play
Sequence diagram generated at: output/cat-play-seq
```

출력:
```
=== Java Source UML Generator ===
Source directory: sample
Output base path: output/sample-diagram

--- Analyzing Java Source Files ---
Found 6 classes

Classes analyzed:
  - abstract class sample.Animal
  - class sample.Cat
  - class sample.Dog
  - class sample.Food
  - enum sample.FoodType
  - interface sample.Mammal
  - interface sample.Pet

--- Generating UML Diagrams ---
PlantUML file saved to: output/sample-diagram.puml
UML diagram image saved to: output/sample-diagram.png
UML diagram image saved to: output/sample-diagram.svg

=== UML Generation Complete ===
PlantUML file: output/sample-diagram.puml
PNG diagram: output/sample-diagram.png
SVG diagram: output/sample-diagram.svg
```

## Dependencies / 의존성

- **JavaParser** (3.25.8): 자바 소스 코드 파싱
- **PlantUML** (1.2023.13): UML 다이어그램 생성
- **SLF4J** (2.0.9): 로깅

## UML Notation / UML 표기법

생성된 다이어그램은 다음 표기법을 사용합니다:

- `+` : public
- `-` : private
- `#` : protected
- `~` : package-private

관계:
- `<|--` : 상속 (inheritance)
- `<|..` : 인터페이스 구현 (interface implementation)
- `..>` : 의존 관계 (dependency/uses)

## Limitations / 제한사항

- 내부 클래스(Inner classes)는 현재 지원되지 않습니다
- 제네릭 타입 파라미터는 단순화되어 표시됩니다
- java.lang, java.util 패키지의 클래스는 의존성에서 제외됩니다

## Troubleshooting / 문제 해결

### Gradle 명령어가 실행되지 않는 경우

Windows에서는 `gradlew`, Linux/Mac에서는 `./gradlew`를 사용하세요.

### 이미지가 생성되지 않는 경우

Graphviz가 시스템에 설치되어 있는지 확인하세요. PlantUML은 일부 다이어그램 생성에 Graphviz를 사용합니다.

설치:
- Windows: `choco install graphviz` (Chocolatey 사용)
- Mac: `brew install graphviz`
- Linux: `sudo apt-get install graphviz` (Ubuntu/Debian)

### Out of Memory 오류

큰 프로젝트의 경우 메모리를 늘려주세요:
```bash
export GRADLE_OPTS="-Xmx2g"
gradlew run --args="large-project"
```

## License

MIT License

## Contributing

이슈나 개선 사항은 GitHub Issues를 통해 제보해주세요.

Issues and improvements can be reported via GitHub Issues.
