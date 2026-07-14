import os
import json
import sys

def create_item_models(item_names, output_dir="src/main/resources/assets/hbm/models/item"):
    """
    Создаёт JSON файлы моделей для указанных предметов
    Модель ссылается на модель блока
    """
    
    # Создаём директорию, если её нет
    os.makedirs(output_dir, exist_ok=True)
    
    # Шаблон для предмета
    template = {
        "parent": "hbm:block/{name}"
    }
    
    # Если предмет в подпапке (например, deco/)
    template_subfolder = {
        "parent": "hbm:block/{full_path}"
    }
    
    created_count = 0
    
    for item in item_names:
        item = item.strip()
        if not item:
            continue
        
        # Проверяем, есть ли подпапка
        if '/' in item:
            # Например "deco/deco_asbestos"
            parts = item.split('/')
            name = parts[-1]
            block_path = item
        else:
            name = item
            block_path = name
        
        # Создаём копию шаблона
        template_copy = {
            "parent": f"hbm:block/{block_path}"
        }
        
        file_path = os.path.join(output_dir, f"{name}.json")
        
        with open(file_path, 'w', encoding='utf-8') as f:
            json.dump(template_copy, f, indent=2, ensure_ascii=False)
        
        print(f"✓ Создан: {file_path}")
        created_count += 1
    
    return created_count

def create_batch_from_list(items_list, output_dir="item"):
    """Создаёт модели из списка предметов"""
    return create_item_models(items_list, output_dir)

if __name__ == "__main__":
    # Список предметов из твоего сообщения
    items = [
        "plant_tall",
        "dirt_dead",
        "dirt_oily",
        "ore_alexandrite",
        "gas_flammable",
        "gas_explosive",
        "block_schrabidium_cluster",
        "block_euphemium_cluster",
        "det_cord",
        "det_charge",
        "toxic_block",
        "nuke_custom",
        "block_slag",
        "bobble"
    ]
    
    # Или можно передать через аргумент командной строки
    if len(sys.argv) > 1:
        items = [item.strip() for item in sys.argv[1].split(',')]
    
    print(f"Создание моделей для {len(items)} предметов...")
    count = create_batch_from_list(items)
    print(f"\n✅ Готово! Создано {count} файлов моделей.")